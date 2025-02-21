package org.romanzhula.expenses_service.services;

import lombok.RequiredArgsConstructor;
import org.romanzhula.expenses_service.models.Expense;
import org.romanzhula.expenses_service.repositories.ExpenseRepository;
import org.romanzhula.expenses_service.requests.BalanceUpdateRequest;
import org.romanzhula.expenses_service.requests.ExpenseRequest;
import org.romanzhula.expenses_service.requests.JournalEntryRequest;
import org.romanzhula.expenses_service.responses.ExpenseResponse;
import org.romanzhula.expenses_service.responses.JournalEntryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    @Value("${wallet.service.getBalanceUrl}")
    private String walletServiceGetBalanceUrl;

    @Value("${wallet.service.updateBalanceUrl}")
    private String walletServiceUpdateBalanceUrl;

    @Value("${journal.service.addEntryUrl}")
    private String journalEntryRequestUrl;

    private final WebClient webClient;
    private final ExpenseRepository expenseRepository;


    @Transactional
    public ExpenseResponse addExpense(ExpenseRequest expenseRequest) {
        UUID userId = UUID.fromString(expenseRequest.getUserId());
        BigDecimal expenseAmount = expenseRequest.getAmount();

        BigDecimal currentBalance = fetchUserBalance(userId);
        validateSufficientFunds(currentBalance, expenseAmount);

        Expense savedExpense = saveExpense(userId, expenseRequest.getTitle(), expenseAmount);
        updateWalletBalance(userId, expenseAmount);

        BigDecimal updatedBalance = fetchUserBalance(userId);
        recordJournalEntry(userId, expenseAmount);

        return new ExpenseResponse(
                savedExpense.getId(),
                savedExpense.getUserId(),
                savedExpense.getTitle(),
                savedExpense.getAmount(),
                "Your balance successfully deducted!",
                updatedBalance
        );
    }


    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAllExpensesByUserId(String userId) {
        return expenseRepository.findAllByUserId(UUID.fromString(userId))
                .stream()
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getUserId(),
                        expense.getTitle(),
                        expense.getAmount(),
                        "",
                        expense.getAmount()
                ))
                .toList();
    }


    private BigDecimal fetchUserBalance(UUID userId) {
        String getBalanceUrl = walletServiceGetBalanceUrl.replace("{userId}", userId.toString());

        return webClient.get()
                .uri(getBalanceUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, BigDecimal>>() {})
                .map(response -> response.get("balance"))
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve user balance"))
        ;
    }


    private void validateSufficientFunds(BigDecimal balance, BigDecimal expenseAmount) {
        if (balance.compareTo(expenseAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for the expense");
        }
    }


    private Expense saveExpense(UUID userId, String title, BigDecimal amount) {
        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setTitle(title);
        expense.setAmount(amount);
        return expenseRepository.save(expense);
    }


    private void updateWalletBalance(UUID userId, BigDecimal expenseAmount) {
        BalanceUpdateRequest balanceUpdateRequest = new BalanceUpdateRequest(userId.toString(), expenseAmount);

        String responseMessage = webClient.patch()
                .uri(walletServiceUpdateBalanceUrl)
                .bodyValue(balanceUpdateRequest)
                .retrieve()
                .bodyToMono(String.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to update wallet balance"))
        ;

        if (!"Your balance successfully deducted!".equals(responseMessage)) {
            throw new RuntimeException("Wallet service failed to deduct balance");
        }
    }

    private void recordJournalEntry(UUID userId, BigDecimal expenseAmount) {
        JournalEntryRequest journalEntryRequest = new JournalEntryRequest(
                userId, "Your balance was updated successfully! -" + expenseAmount);

        JournalEntryResponse response = webClient.post()
                .uri(journalEntryRequestUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(journalEntryRequest)
                .retrieve()
                .bodyToMono(JournalEntryResponse.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to add journal entry"))
        ;

        if (!"New journal entry was added successfully.".equals(response.getMessage())) {
            throw new RuntimeException("Journal service error: " + response.getMessage());
        }
    }

}
