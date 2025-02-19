package org.romanzhula.expenses_service.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BalanceUpdateRequest {

    private String id;
    private BigDecimal amount;

}
