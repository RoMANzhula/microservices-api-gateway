package org.romanzhula.journal_service.requests;

import lombok.Getter;

import java.util.UUID;

@Getter
public class JournalEntryRequest {

    private UUID userId;
    private String description;

}
