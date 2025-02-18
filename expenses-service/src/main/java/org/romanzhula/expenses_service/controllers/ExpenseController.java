package org.romanzhula.expenses_service.controllers;

import lombok.RequiredArgsConstructor;
import org.romanzhula.expenses_service.requests.ExpenseRequest;
import org.romanzhula.expenses_service.responses.ExpenseResponse;
import org.romanzhula.expenses_service.services.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/add-expense")
    public ResponseEntity<ExpenseResponse> addExpense(
            @RequestBody ExpenseRequest expenseRequest
    ) {
        return ResponseEntity.ok(expenseService.addExpense(expenseRequest));
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<List<ExpenseResponse>> getAllExpensesByUserId(
            @PathVariable("user-id") String userId
    ) {
        return ResponseEntity.ok(expenseService.getAllExpensesByUserId(userId));
    }

}
