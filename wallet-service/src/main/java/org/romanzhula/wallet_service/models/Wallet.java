package org.romanzhula.wallet_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "wallets")
public class Wallet {
    // TODO: here we need logic for using userId from user-service as UUID, we will add RabbitMQ and JWT for this logic
    // TODO: add logic for security verification wallet-vs-user
    @Id
    @Column(name = "user_id", unique = true, updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;

    // TODO: add mail sender balance data to email (get email from user-service)
}
