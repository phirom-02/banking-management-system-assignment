package com.firom.bms.services.impl;

import com.firom.bms.dto.transaction.DepositRequest;
import com.firom.bms.dto.transaction.TransactionResponse;
import com.firom.bms.dto.transaction.TransferRequest;
import com.firom.bms.dto.transaction.WithdrawalRequest;
import com.firom.bms.entity.Account;
import com.firom.bms.entity.Admin;
import com.firom.bms.entity.LedgerEntry;
import com.firom.bms.entity.Transaction;
import com.firom.bms.enums.AccountStatus;
import com.firom.bms.enums.EntryType;
import com.firom.bms.enums.TransactionStatus;
import com.firom.bms.enums.TransactionType;
import com.firom.bms.exception.InsufficientFundsException;
import com.firom.bms.exception.InvalidAccountStateException;
import com.firom.bms.exception.ResourceNotFoundException;
import com.firom.bms.repository.AccountRepository;
import com.firom.bms.repository.AdminRepository;
import com.firom.bms.repository.LedgerEntryRepository;
import com.firom.bms.repository.TransactionRepository;
import com.firom.bms.security.AdminUserDetails;
import com.firom.bms.services.TransactionService;
import com.firom.bms.util.IdentifierGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SuppressWarnings("all")
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        Account account = lockAccount(request.getAccountNumber());
        assertActive(account);

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = saveTransaction(
                TransactionType.DEPOSIT, null, account, request.getAmount(),
                request.getDescription(), TransactionStatus.COMPLETED);

        // Double entry: CREDIT the customer account (external cash source is implicitly debited)
        recordLedgerEntry(transaction, account, EntryType.CREDIT, request.getAmount(), account.getBalance());

        return toResponse(transaction, account, null);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest request) {
        Account account = lockAccount(request.getAccountNumber());
        assertActive(account);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account " + account.getAccountNumber());
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = saveTransaction(
                TransactionType.WITHDRAWAL, account, null, request.getAmount(),
                request.getDescription(), TransactionStatus.COMPLETED);

        // Double entry: DEBIT the customer account (external cash disbursed)
        recordLedgerEntry(transaction, account, EntryType.DEBIT, request.getAmount(), account.getBalance());

        return toResponse(transaction, account, null);
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.getSourceAccountNumber().equals(request.getDestinationAccountNumber())) {
            throw new InvalidAccountStateException("Source and destination accounts must differ");
        }

        // Lock accounts in a deterministic order (by account number) to avoid deadlocks
        // when two transfers between the same pair of accounts run concurrently in opposite directions.
        String first = request.getSourceAccountNumber().compareTo(request.getDestinationAccountNumber()) < 0
                ? request.getSourceAccountNumber() : request.getDestinationAccountNumber();
        String second = first.equals(request.getSourceAccountNumber())
                ? request.getDestinationAccountNumber() : request.getSourceAccountNumber();

        Account firstLocked = lockAccount(first);
        Account secondLocked = lockAccount(second);

        Account source = firstLocked.getAccountNumber().equals(request.getSourceAccountNumber()) ? firstLocked : secondLocked;
        Account destination = firstLocked.getAccountNumber().equals(request.getDestinationAccountNumber()) ? firstLocked : secondLocked;

        assertActive(source);
        assertActive(destination);

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account " + source.getAccountNumber());
        }

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        destination.setBalance(destination.getBalance().add(request.getAmount()));
        accountRepository.save(source);
        accountRepository.save(destination);

        Transaction transaction = saveTransaction(
                TransactionType.TRANSFER, source, destination, request.getAmount(),
                request.getDescription(), TransactionStatus.COMPLETED);

        // Double entry: DEBIT source, CREDIT destination — amounts match, ledger stays balanced
        recordLedgerEntry(transaction, source, EntryType.DEBIT, request.getAmount(), source.getBalance());
        recordLedgerEntry(transaction, destination, EntryType.CREDIT, request.getAmount(), destination.getBalance());

        return toResponse(transaction, source, destination);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getByReference(String reference) {
        Transaction t = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + reference));
        return toResponse(t, t.getSourceAccount(), t.getDestinationAccount());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> listForAccount(Long accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable)
                .map(t -> toResponse(t, t.getSourceAccount(), t.getDestinationAccount()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> listAll(Pageable pageable) {
        return transactionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(t -> toResponse(t, t.getSourceAccount(), t.getDestinationAccount()));
    }

    // ---- helpers -------------------------------------------------------

    private Account lockAccount(String accountNumber) {
        return accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }

    private void assertActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException(
                    "Account " + account.getAccountNumber() + " is " + account.getStatus() + " and cannot transact");
        }
    }

    private Transaction saveTransaction(TransactionType type, Account source, Account destination,
                                        BigDecimal amount, String description, TransactionStatus status) {
        String reference;
        do {
            reference = IdentifierGenerator.generateTransactionReference();
        } while (transactionRepository.findByReference(reference).isPresent());

        Transaction transaction = new Transaction();

        transaction.setReference(reference);
        transaction.setType(type);
        transaction.setSourceAccount(source);
        transaction.setDestinationAccount(destination);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setStatus(status);
        transaction.setPerformedBy(currentAdmin());

        return transactionRepository.save(transaction);
    }

    private void recordLedgerEntry(Transaction transaction, Account account, EntryType entryType,
                                   BigDecimal amount, BigDecimal balanceAfter) {
        LedgerEntry entry = new LedgerEntry();

        entry.setTransaction(transaction);
        entry.setAccount(account);
        entry.setEntryType(entryType);
        entry.setAmount(amount);
        entry.setBalanceAfter(balanceAfter);

        ledgerEntryRepository.save(entry);
    }

    private Admin currentAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AdminUserDetails details)) {
            return null;
        }
        return adminRepository.findByUsername(details.getUsername()).orElse(null);
    }

    private TransactionResponse toResponse(Transaction t, Account source, Account destination) {
        TransactionResponse response = new TransactionResponse();

        response.setId(t.getId());
        response.setReference(t.getReference());
        response.setType(t.getType());
        response.setSourceAccountNumber(source != null ? source.getAccountNumber() : null);
        response.setDestinationAccountNumber(destination != null ? destination.getAccountNumber() : null);
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
