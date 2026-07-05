package com.firom.bms.dto.account;

import com.firom.bms.enums.AccountType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@SuppressWarnings("all")
public class AccountRequest {

    @NotNull
    private Integer customerId;

    @NotNull
    private AccountType accountType;

    private BigDecimal openingBalance;

    private String currency;

    public AccountRequest(Integer customerId, AccountType accountType, BigDecimal openingBalance, String currency) {
        this.customerId = customerId;
        this.accountType = accountType;
        this.openingBalance = openingBalance;
        this.currency = currency;
    }

    public @NotNull Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@NotNull Integer customerId) {
        this.customerId = customerId;
    }

    public @NotNull AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(@NotNull AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
