package org.romanzhula.expenses_service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ExpenseRequest {

    private String userId;
    private String title;

    @JsonProperty("amount")
    private BigDecimal amount;

}
