package com.firom.bms.services.impl;

import com.firom.bms.dto.Combo;
import com.firom.bms.dto.account.AccountComboMetadata;
import com.firom.bms.dto.account.AccountRequest;
import com.firom.bms.dto.account.AccountResponse;
import com.firom.bms.entity.Account;
import com.firom.bms.entity.Customer;
import com.firom.bms.enums.AccountStatus;
import com.firom.bms.enums.CustomerStatus;
import com.firom.bms.exception.InvalidAccountStateException;
import com.firom.bms.exception.ResourceNotFoundException;
import com.firom.bms.repository.AccountRepository;
import com.firom.bms.repository.CustomerRepository;
import com.firom.bms.services.AccountService;
import com.firom.bms.util.IdentifierGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("all")
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public AccountResponse open(AccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new InvalidAccountStateException("Cannot open an account for a non-active customer");
        }

        String accountNumber;
        do {
            accountNumber = IdentifierGenerator.generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setCustomer(customer);
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getOpeningBalance() != null ? request.getOpeningBalance() : BigDecimal.ZERO);
        account.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        account.setStatus(AccountStatus.ACTIVE);

        account = accountRepository.save(account);
        return toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getById(Integer id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        return toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponse> list(Integer customerId, AccountStatus status, Pageable pageable) {
        Page<Account> page;
        if (customerId != null) {
            page = accountRepository.findByCustomerId(customerId, pageable);
        } else if (status != null) {
            page = accountRepository.findByStatus(status, pageable);
        } else {
            page = accountRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    @Override
    @Transactional
    public AccountResponse updateStatus(Integer id, AccountStatus newStatus) {
        Account account = findEntity(id);

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStateException("Cannot change status of a closed account");
        }
        if (newStatus == AccountStatus.CLOSED && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidAccountStateException("Account balance must be zero before it can be closed");
        }

        account.setStatus(newStatus);
        return toResponse(accountRepository.save(account));
    }

    @Override
    public List<Combo<AccountComboMetadata>> combo(Integer customerId, String status) {
        java.util.List<Account> accounts;
        boolean hasStatus = status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status);
        AccountStatus parsedStatus = hasStatus ? AccountStatus.valueOf(status.toUpperCase()) : null;

        if (customerId != null && hasStatus) {
            accounts = accountRepository.findByCustomerIdAndStatusOrderByAccountNumberAsc(customerId, parsedStatus);
        } else if (customerId != null) {
            accounts = accountRepository.findByCustomerIdOrderByAccountNumberAsc(customerId);
        } else if (hasStatus) {
            accounts = accountRepository.findByStatusOrderByAccountNumberAsc(parsedStatus);
        } else {
            accounts = accountRepository.findAllByOrderByAccountNumberAsc();
        }

        return accounts.stream().map(this::toCombo).toList();
    }

    private Combo<AccountComboMetadata> toCombo(Account a) {
        String label = a.getAccountNumber() + " — " + a.getCustomer().getFullName()
                + " (" + a.getAccountType() + ", " + a.getCurrency() + " " + a.getBalance()
                + ", " + a.getStatus() + ")";

        AccountComboMetadata metadata = new AccountComboMetadata();
        metadata.setId(a.getId());
        metadata.setCustomerId(a.getCustomer().getId());
        metadata.setCustomerName(a.getCustomer().getFullName());
        metadata.setAccountType(a.getAccountType().name());
        metadata.setBalance(a.getBalance());
        metadata.setCurrency(a.getCurrency());
        metadata.setStatus(a.getStatus().name());

        Combo<AccountComboMetadata> combo = new Combo<>();
        combo.setLabel(label);
        combo.setValue(a.getAccountNumber());
        combo.setMetadata(metadata);

        return combo;
    }

    private Account findEntity(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    private AccountResponse toResponse(Account a) {
        AccountResponse response = new AccountResponse();
        response.setId(a.getId());
        response.setAccountNumber(a.getAccountNumber());
        response.setCustomerId(a.getCustomer().getId());
        response.setAccountType(a.getAccountType());
        response.setCustomerName(a.getCustomer().getFullName());
        response.setAccountType(a.getAccountType());
        response.setBalance(a.getBalance());
        response.setCurrency(a.getCurrency());
        response.setStatus(a.getStatus());
        response.setCreatedAt(a.getCreatedAt());

        return response;
    }
}
