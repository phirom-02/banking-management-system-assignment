package com.firom.bms.dto.account;

import com.firom.bms.enums.AccountStatus;
import com.firom.bms.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SuppressWarnings("all")
public class AccountResponse {
    private Integer id;
    private String accountNumber;
    private Integer customerId;
    private String customerName;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
    private LocalDateTime createdAt;

    public AccountResponse() {
    }

    public AccountResponse(Integer id, String accountNumber, Integer customerId, String customerName, AccountType accountType, BigDecimal balance, String currency, AccountStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
