package com.firom.bms.dto.account;

import java.math.BigDecimal;

@SuppressWarnings("all")
public class AccountComboMetadata {
    private Integer id;
    private Integer customerId;
    private String customerName;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private String status;

    public AccountComboMetadata() {
    }

    public AccountComboMetadata(Integer id, Integer customerId, String customerName, String accountType, BigDecimal balance, String currency, String status) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
