package com.firom.bms.services;

import com.firom.bms.dto.ledger.LedgerEntryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LedgerService {
    Page<LedgerEntryResponse> listForAccount(Long accountId, Pageable pageable);

    Page<LedgerEntryResponse> listForTransaction(Long transactionId, Pageable pageable);

    Page<LedgerEntryResponse> listAll(Pageable pageable);
}
