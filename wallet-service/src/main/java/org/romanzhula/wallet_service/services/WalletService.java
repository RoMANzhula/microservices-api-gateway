package org.romanzhula.wallet_service.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.romanzhula.wallet_service.models.Wallet;
import org.romanzhula.wallet_service.repositories.WalletRepository;
import org.romanzhula.wallet_service.requests.BalanceUpdateRequest;
import org.romanzhula.wallet_service.requests.JournalEntryRequest;
import org.romanzhula.wallet_service.responses.JournalEntryResponse;
import org.romanzhula.wallet_service.responses.WalletBalanceResponse;
import org.romanzhula.wallet_service.responses.WalletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    @Value("${journal.service.addEntryUrl}")
    private String journalEntryRequestUrl;

    private final WalletRepository walletRepository;
    private final WebClient webClient;


    @Transactional(readOnly = true)
    public WalletResponse getWalletById(UUID id) {
        return walletRepository.findById(id)
                .map(wallet -> new WalletResponse(wallet.getUserId(), wallet.getBalance()))
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + id))
        ;
    }


    @Transactional
    public String updateBalance(BalanceUpdateRequest request) {
        validateBalanceUpdateRequest(request);

        Wallet wallet = fetchWalletById(request.getId());
        updateWalletBalance(wallet, request.getAmount());

        JournalEntryResponse journalResponse = createJournalEntry(request);
        validateJournalEntryResponse(journalResponse);

        return "Your balance was updated successfully!";
    }


    @Transactional(readOnly = true)
    public List<WalletResponse> getAll() {
        return walletRepository.findAll()
                .stream()
                .map(wallet -> new WalletResponse(wallet.getUserId(), wallet.getBalance()))
                .collect(Collectors.toList())
        ;
    }


    @Transactional(readOnly = true)
    public WalletBalanceResponse getWalletBalanceById(UUID id) {
        return walletRepository.findById(id)
                .map(wallet -> new WalletBalanceResponse(wallet.getBalance()))
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + id))
        ;
    }


    @Transactional
    public String deductBalance(BalanceUpdateRequest request) {
        validateDeductionRequest(request);

        Wallet wallet = fetchWalletById(request.getId());
        checkSufficientFunds(wallet, request.getAmount());

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        return "Your balance successfully deducted!";
    }


    private void validateBalanceUpdateRequest(BalanceUpdateRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The top-up amount must be greater than 0.");
        }
    }


    private Wallet fetchWalletById(String walletId) {
        return walletRepository.findById(UUID.fromString(walletId))
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
    }


    private void updateWalletBalance(Wallet wallet, BigDecimal amount) {
        if (wallet == null) {
            handleUserCreated(wallet.getUserId().toString());
        } else {
            wallet.setBalance(wallet.getBalance().add(amount));
        }

        walletRepository.save(wallet);
    }


    private JournalEntryResponse createJournalEntry(BalanceUpdateRequest request) {

        JournalEntryRequest journalEntryRequest = new JournalEntryRequest(
                UUID.fromString(request.getId()),
                "Your balance was updated successfully! +" + request.getAmount()
        );

        return webClient.post()
                .uri(journalEntryRequestUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(journalEntryRequest)
                .retrieve()
                .bodyToMono(JournalEntryResponse.class)
                .block()
        ;
    }


    private void validateJournalEntryResponse(JournalEntryResponse journalResponse) {
        if (journalResponse == null || !"New journal entry was added successfully.".equals(journalResponse.getMessage())) {
            throw new RuntimeException("Failed to update wallet balance FOR JOURNAL_SERVICE");
        }
    }


    private void validateDeductionRequest(BalanceUpdateRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The deduction amount must be greater than 0.");
        }

        if (request.getId() == null || request.getId().isEmpty()) {
            throw new IllegalArgumentException("Wallet ID cannot be null or empty.");
        }
    }


    private void checkSufficientFunds(Wallet wallet, BigDecimal deductionAmount) {
        if (wallet.getBalance().compareTo(deductionAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the wallet.");
        }
    }


    public void handleUserCreated(String id) {
        Wallet wallet = new Wallet();
        wallet.setUserId(UUID.randomUUID());
        wallet.setBalance(BigDecimal.valueOf(0.0));
        walletRepository.save(wallet);
    }

}
