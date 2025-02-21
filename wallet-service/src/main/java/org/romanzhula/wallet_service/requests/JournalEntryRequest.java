package org.romanzhula.wallet_service.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class JournalEntryRequest {

    private UUID userId;
    private String description;

}
