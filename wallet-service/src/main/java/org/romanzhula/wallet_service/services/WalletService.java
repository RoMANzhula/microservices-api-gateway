package org.romanzhula.wallet_service.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.romanzhula.wallet_service.models.Wallet;
import org.romanzhula.wallet_service.repositories.WalletRepository;
import org.romanzhula.wallet_service.requests.BalanceUpdateRequest;
import org.romanzhula.wallet_service.responses.WalletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    
    @Transactional(readOnly = true)
    public WalletResponse getWalletById(String id) {
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
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The top-up amount must be greater than 0.");
        }

        Wallet wallet = walletRepository
                .findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + request.getId()));

        if (wallet == null) {
            handleUserCreated(request.getId());
        } else {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        }

        walletRepository.save(wallet);

        return "Your balance was updated successfully!";
    }

    // TODO: change to new method with @RabbitListener
    // temporary method
    public void handleUserCreated(String id) {
        Wallet wallet = new Wallet();
        wallet.setUserId(id);
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

}
