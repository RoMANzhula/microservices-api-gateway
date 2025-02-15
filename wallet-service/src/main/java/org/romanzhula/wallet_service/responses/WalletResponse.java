package org.romanzhula.wallet_service.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class WalletResponse {

    private String userId;
    private BigDecimal balance;

}
