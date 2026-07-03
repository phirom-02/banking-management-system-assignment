package com.firom.bms.services.impl;

import com.firom.bms.dto.ledger.LedgerEntryResponse;
import com.firom.bms.services.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {
    @Override
    public Page<LedgerEntryResponse> listForAccount(Long accountId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<LedgerEntryResponse> listForTransaction(Long transactionId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<LedgerEntryResponse> listAll(Pageable pageable) {
        return null;
    }
}
