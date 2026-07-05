package com.firom.bms.repository;

import com.firom.bms.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findByReference(String reference);

    @Query("select t from Transaction t where t.sourceAccount.id = :accountId or t.destinationAccount.id = :accountId")
    Page<Transaction> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);

    Page<Transaction> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
