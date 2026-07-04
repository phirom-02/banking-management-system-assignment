package com.firom.bms.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@SuppressWarnings("all")
public class DepositRequest {

    @NotBlank
    private String accountNumber;

    public DepositRequest(String accountNumber, BigDecimal amount, String description) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.description = description;
    }

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String description;

    public @NotBlank String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(@NotBlank String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
