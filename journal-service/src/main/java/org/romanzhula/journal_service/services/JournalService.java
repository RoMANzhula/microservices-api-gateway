package org.romanzhula.journal_service.services;

import lombok.RequiredArgsConstructor;
import org.romanzhula.journal_service.models.JournalEntry;
import org.romanzhula.journal_service.repositories.JournalRepository;
import org.romanzhula.journal_service.requests.JournalEntryRequest;
import org.romanzhula.journal_service.responses.JournalEntryResponse;
import org.romanzhula.journal_service.responses.JournalResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository journalRepository;

    
    @Transactional(readOnly = true)
    public List<JournalResponse> getAllEntries() {
        return journalRepository.findAll()
                .stream()
                .map(journalEntry -> new JournalResponse(
                        journalEntry.getId(),
                        journalEntry.getUserId(),
                        journalEntry.getDescription(),
                        journalEntry.getCreatedAt()
                ))
                .toList()
        ;
    }

    @Transactional(readOnly = true)
    public List<JournalResponse> getAllUserJournalEntries(String userId) {
        return journalRepository.findAllByUserId(UUID.fromString(userId))
                .stream()
                .map(journalEntry -> new JournalResponse(
                        journalEntry.getId(),
                        journalEntry.getUserId(),
                        journalEntry.getDescription(),
                        journalEntry.getCreatedAt()
                ))
                .toList()
        ;
    }

    @Transactional
    public JournalEntryResponse addNewJournalEntry(JournalEntryRequest journalEntryRequest) {
        JournalEntry newJournalEntry = new JournalEntry();

        newJournalEntry.setUserId(journalEntryRequest.getUserId());
        newJournalEntry.setDescription(journalEntryRequest.getDescription());

        journalRepository.save(newJournalEntry);

        return new JournalEntryResponse("New journal entry was added successfully.");
    }

}
