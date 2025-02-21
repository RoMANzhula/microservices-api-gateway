package org.romanzhula.wallet_service.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.romanzhula.wallet_service.models.Wallet;
import org.romanzhula.wallet_service.repositories.WalletRepository;
import org.romanzhula.wallet_service.requests.BalanceUpdateRequest;
import org.romanzhula.wallet_service.requests.JournalEntryRequest;
import org.romanzhula.wallet_service.responses.WalletBalanceResponse;
import org.romanzhula.wallet_service.responses.WalletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WebClient webClient;

    
    @Transactional(readOnly = true)
    public WalletResponse getWalletById(UUID id) {
        return walletRepository.findById(id)
                .map(wallet -> new WalletResponse(
                        wallet.getUserId(),
                        wallet.getBalance()
                ))
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + id))
        ;
    }

    @Transactional
    public String updateBalance(BalanceUpdateRequest request) {
        String journalEntryRequestUrl = "http://localhost:8083/api/v1/journal/add";

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The top-up amount must be greater than 0.");
        }

        Wallet wallet = walletRepository
                .findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + request.getId()));

        if (wallet == null) {
            handleUserCreated(request.getId());
        } else {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        }

        walletRepository.save(wallet);

        JournalEntryRequest journalEntryRequest = new JournalEntryRequest(
                UUID.fromString(request.getId()),
                "Your balance was updated successfully! +" + request.getAmount()
        );

        String successResponse = webClient
                .post()
                .uri(journalEntryRequestUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(journalEntryRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block()
        ;

        if (!"New journal entry was added successfully.".equals(successResponse)) {
            throw new RuntimeException("Failed to update wallet balance FOR JOURNAL_SERVICE");
        }

        return "Your balance was updated successfully!";
    }

    // TODO: change to new method with @RabbitListener
    // temporary method
    public void handleUserCreated(String id) {
        Wallet wallet = new Wallet();
        wallet.setUserId(UUID.randomUUID());
        wallet.setBalance(BigDecimal.valueOf(0.0));

        walletRepository.save(wallet);
    }

    // TODO: add pagination or delete this logic for all elements
    @Transactional(readOnly = true)
    public List<WalletResponse> getAll() {
        return walletRepository.findAll().stream()
                .map(wallet -> new WalletResponse(
                        wallet.getUserId(),
                        wallet.getBalance()
                ))
                .toList()
        ;
    }

    @Transactional(readOnly = true)
    public WalletBalanceResponse getWalletBalanceById(UUID id) {
        return walletRepository.findById(id)
                .map(wallet -> new WalletBalanceResponse(
                        wallet.getBalance()
                ))
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + id))
        ;
    }

    @Transactional
    public String deductBalance(BalanceUpdateRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The deduction amount must be greater than 0.");
        }

        if (request.getId() == null || request.getId().isEmpty()) {
            throw new IllegalArgumentException("Wallet ID cannot be null or empty.");
        }

        UUID walletId = UUID.fromString(request.getId());

        Wallet wallet = walletRepository
                .findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id:" + request.getId()))
        ;

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the wallet.");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        return "Your balance successfully deducted!";
    }

}
