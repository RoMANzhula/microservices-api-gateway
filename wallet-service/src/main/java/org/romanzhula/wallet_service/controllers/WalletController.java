package org.romanzhula.wallet_service.controllers;

import lombok.RequiredArgsConstructor;
import org.romanzhula.wallet_service.requests.BalanceUpdateRequest;
import org.romanzhula.wallet_service.responses.WalletResponse;
import org.romanzhula.wallet_service.services.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;


    @GetMapping("/{wallet-id}") // here we will use user id as wallet id
    public ResponseEntity<WalletResponse> getWalletById(
            @PathVariable("wallet-id") String id
    ) {
        return ResponseEntity.ok(walletService.getWalletById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<WalletResponse>> getAll() {
        return ResponseEntity.ok(walletService.getAll());
    }

    @PostMapping("/up-balance")
    public ResponseEntity<String> updateBalance(
            // TODO: add AuthPrincipal when will be Security
            @RequestBody BalanceUpdateRequest request
    ) {
        return ResponseEntity.ok(walletService.updateBalance(request));
    }

}
