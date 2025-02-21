package org.romanzhula.journal_service.controllers;

import lombok.RequiredArgsConstructor;
import org.romanzhula.journal_service.requests.JournalEntryRequest;
import org.romanzhula.journal_service.responses.JournalEntryResponse;
import org.romanzhula.journal_service.responses.JournalResponse;
import org.romanzhula.journal_service.services.JournalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/journal")
public class JournalController {

    private final JournalService journalService;

    
    @GetMapping("/{user-id}")
    public ResponseEntity<List<JournalResponse>> getAllUserJournalEntries(
            @PathVariable("user-id") String userId
    ) {
        return ResponseEntity.ok(journalService.getAllUserJournalEntries(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<JournalResponse>> getAllEntries() {
        return ResponseEntity.ok(journalService.getAllEntries());
    }

    @PostMapping("/add")
    public ResponseEntity<JournalEntryResponse> addNewJournalEntry(
            @RequestBody JournalEntryRequest journalEntryRequest
    ) {
        return ResponseEntity.ok(journalService.addNewJournalEntry(journalEntryRequest));
    }

}
