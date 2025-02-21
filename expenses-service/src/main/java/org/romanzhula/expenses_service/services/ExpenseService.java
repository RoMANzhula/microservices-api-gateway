package org.romanzhula.expenses_service.services;

import lombok.RequiredArgsConstructor;
import org.romanzhula.expenses_service.models.Expense;
import org.romanzhula.expenses_service.repositories.ExpenseRepository;
import org.romanzhula.expenses_service.requests.BalanceUpdateRequest;
import org.romanzhula.expenses_service.requests.ExpenseRequest;
import org.romanzhula.expenses_service.requests.JournalEntryRequest;
import org.romanzhula.expenses_service.responses.ExpenseResponse;
import org.romanzhula.expenses_service.responses.JournalEntryResponse;
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

    private final WebClient webClient;
    private final ExpenseRepository expenseRepository;


    @Transactional
    public ExpenseResponse addExpense(ExpenseRequest expenseRequest) {
        String userId = expenseRequest.getUserId();
        String walletServiceGetBalanceUrl = "http://localhost:8082/api/v1/wallets/" + userId + "/balance";
        String walletServiceUpdateBalanceUrl = "http://localhost:8082/api/v1/wallets/deduct-balance";
        String journalEntryRequestUrl = "http://localhost:8083/api/v1/journal/add";

        BigDecimal currentBalance = webClient
                .get()
                .uri(walletServiceGetBalanceUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, BigDecimal>>() {})
                .map(response -> response.get("balance"))
                .block();

        if (currentBalance == null) {
            throw new RuntimeException("Failed to retrieve user balance");
        }

        BigDecimal expenseAmount = expenseRequest.getAmount();

        if (currentBalance.compareTo(expenseAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for the expense");
        }

        Expense expense = new Expense();
        expense.setUserId(UUID.fromString(expenseRequest.getUserId()));
        expense.setTitle(expenseRequest.getTitle());
        expense.setAmount(expenseAmount);

        Expense savedExpense = expenseRepository.save(expense);

        // TODO: add RabbitMQ convertAndSend here to change balance (wallet-service)

        BalanceUpdateRequest balanceUpdateRequest = new BalanceUpdateRequest(userId, expenseAmount);

        String successMessage = webClient
                .patch()
                .uri(walletServiceUpdateBalanceUrl)
                .bodyValue(balanceUpdateRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block()
        ;

        if (!"Your balance successfully deducted!".equals(successMessage)) {
            throw new RuntimeException("Failed to update wallet balance");
        }

        BigDecimal updatedBalance = webClient
                .get()
                .uri(walletServiceGetBalanceUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, BigDecimal>>() {})
                .map(response -> response.get("balance"))
                .block();

        if (updatedBalance == null) {
            throw new RuntimeException("Failed to retrieve updated balance");
        }

        JournalEntryRequest journalEntryRequest = new JournalEntryRequest(
                UUID.fromString(userId),
                "Your balance was updated successfully! +" + expenseAmount
        );

        JournalEntryResponse successResponse = webClient
                .post()
                .uri(journalEntryRequestUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(journalEntryRequest)
                .retrieve()
                .bodyToMono(JournalEntryResponse.class)
                .block();

        if (!"New journal entry was added successfully.".equals(successResponse.getMessage())) {
            throw new RuntimeException(
                    "Failed to update wallet balance FOR JOURNAL_SERVICE. Response: " + successResponse.getMessage()
            );
        }


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
                .toList()
        ;
    }

}
