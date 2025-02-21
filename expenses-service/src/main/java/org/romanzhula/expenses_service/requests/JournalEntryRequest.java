package org.romanzhula.expenses_service.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class JournalEntryRequest {

    private UUID userId;
    private String description;

}
