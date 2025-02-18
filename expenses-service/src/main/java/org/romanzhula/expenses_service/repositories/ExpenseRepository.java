package org.romanzhula.expenses_service.repositories;

import org.romanzhula.expenses_service.models.Expense;
import org.romanzhula.expenses_service.responses.ExpenseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<ExpenseResponse> findAllByUserId(UUID userId);

}
