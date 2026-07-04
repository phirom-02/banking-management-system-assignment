package com.firom.bms.dto.ledger;

import com.firom.bms.enums.EntryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SuppressWarnings("all")
public class LedgerEntryResponse {
    private Integer id;
    private String transactionReference;
    private String accountNumber;
    private EntryType entryType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;

    public LedgerEntryResponse() {
    }

    public LedgerEntryResponse(Integer id, String transactionReference, String accountNumber, EntryType entryType, BigDecimal amount, BigDecimal balanceAfter, LocalDateTime createdAt) {
        this.id = id;
        this.transactionReference = transactionReference;
        this.accountNumber = accountNumber;
        this.entryType = entryType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
