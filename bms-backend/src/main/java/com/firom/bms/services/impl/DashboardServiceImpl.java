package com.firom.bms.services.impl;

import com.firom.bms.dto.dashboard.DashboardResponse;
import com.firom.bms.dto.transaction.TransactionResponse;
import com.firom.bms.entity.Transaction;
import com.firom.bms.enums.AccountStatus;
import com.firom.bms.enums.CustomerStatus;
import com.firom.bms.repository.AccountRepository;
import com.firom.bms.repository.CustomerRepository;
import com.firom.bms.repository.TransactionRepository;
import com.firom.bms.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SuppressWarnings("all")
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getSummary() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        long transactionsToday = transactionRepository
                .findByCreatedAtBetween(startOfDay, endOfDay, PageRequest.of(0, 1))
                .getTotalElements();

        var recent = transactionRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10))
                .map(this::toTransactionResponse)
                .getContent();

        DashboardResponse response = new DashboardResponse();

        response.setTotalCustomers(customerRepository.count());
        response.setActiveCustomers(customerRepository.countByStatus(CustomerStatus.ACTIVE));
        response.setTotalAccounts(accountRepository.count());
        response.setActiveAccounts(accountRepository.countByStatus(AccountStatus.ACTIVE));
        response.setFrozenAccounts(accountRepository.countByStatus(AccountStatus.FROZEN));
        response.setClosedAccounts(accountRepository.countByStatus(AccountStatus.CLOSED));
        response.setTotalBalanceHeld(accountRepository.sumOfAllActiveBalances());
        response.setTransactionsToday(transactionsToday);
        response.setRecentActivity(recent);

        return response;
    }

    private TransactionResponse toTransactionResponse(Transaction t) {
        TransactionResponse response = new TransactionResponse();

        response.setId(t.getId());
        response.setReference(t.getReference());
        response.setType(t.getType());
        response.setSourceAccountNumber(
                t.getSourceAccount() != null ? t.getSourceAccount().getAccountNumber() : null
        );
        response.setDestinationAccountNumber(
                t.getDestinationAccount() != null ? t.getDestinationAccount().getAccountNumber() : null
        );
        response.setAmount(t.getAmount());
        response.setDescription(t.getDescription());
        response.setStatus(t.getStatus());
        response.setPerformedBy(
                t.getPerformedBy() != null ? t.getPerformedBy().getUsername() : null
        );
        response.setCreatedAt(t.getCreatedAt());

        return response;
    }
}
