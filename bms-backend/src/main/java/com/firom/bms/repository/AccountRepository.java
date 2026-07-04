package com.firom.bms.repository;

import com.firom.bms.entity.Account;
import com.firom.bms.enums.AccountStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    Page<Account> findByCustomerId(Integer customerId, Pageable pageable);

    Page<Account> findByStatus(AccountStatus status, Pageable pageable);

    List<Account> findByCustomerId(Integer customerId);

    long countByStatus(AccountStatus status);

    @Query("select coalesce(sum(a.balance), 0) from Account a where a.status <> 'CLOSED'")
    java.math.BigDecimal sumOfAllActiveBalances();

    // Pessimistic lock used during money-moving operations (deposit/withdraw/transfer)
    // to prevent lost updates under concurrent requests on the same account.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    List<Account> findAllByOrderByAccountNumberAsc();

    List<Account> findByStatusOrderByAccountNumberAsc(AccountStatus status);

    List<Account> findByCustomerIdOrderByAccountNumberAsc(Integer customerId);

    List<Account> findByCustomerIdAndStatusOrderByAccountNumberAsc(Integer customerId, AccountStatus status);

}
