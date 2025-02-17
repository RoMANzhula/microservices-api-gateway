package org.romanzhula.wallet_service.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class WalletResponse {

    private UUID userId;
    private BigDecimal balance;

}
