package org.romanzhula.wallet_service.requests;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BalanceUpdateRequest {

    // temporary field while we add RabbitMQ logic for automation creating wallet
    private String id;

    // TODO: add jakarta validation liba
    //@DecimalMin(value = "0.01", message = "The top-up amount must be greater than 0.")
    private BigDecimal amount;

}
