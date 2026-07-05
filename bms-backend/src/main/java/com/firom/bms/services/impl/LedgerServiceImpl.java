package com.firom.bms.services.impl;

import com.firom.bms.dto.ledger.LedgerEntryResponse;
import com.firom.bms.entity.LedgerEntry;
import com.firom.bms.repository.LedgerEntryRepository;
import com.firom.bms.services.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("all")
@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<LedgerEntryResponse> listForAccount(Long accountId, Pageable pageable) {
        return ledgerEntryRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LedgerEntryResponse> listForTransaction(Long transactionId, Pageable pageable) {
        return ledgerEntryRepository.findByTransactionIdOrderByCreatedAtAsc(transactionId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LedgerEntryResponse> listAll(Pageable pageable) {
        return ledgerEntryRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    private LedgerEntryResponse toResponse(LedgerEntry e) {
        LedgerEntryResponse response = new LedgerEntryResponse();

        response.setId(e.getId());
        response.setTransactionReference(e.getTransaction().getReference());
        response.setAccountNumber(e.getAccount().getAccountNumber());
        response.setEntryType(e.getEntryType());
        response.setAmount(e.getAmount());
        response.setBalanceAfter(e.getBalanceAfter());
        response.setCreatedAt(e.getCreatedAt());

        return response;
    }
}
