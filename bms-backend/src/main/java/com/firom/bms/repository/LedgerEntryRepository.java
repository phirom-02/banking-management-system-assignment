package com.firom.bms.repository;

import com.firom.bms.entity.LedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Integer> {
    Page<LedgerEntry> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    Page<LedgerEntry> findByTransactionIdOrderByCreatedAtAsc(Long transactionId, Pageable pageable);

    Page<LedgerEntry> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
