package com.firom.bms.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@SuppressWarnings("all")
public class TransferRequest {

    public TransferRequest(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount, String description) {
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotBlank String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(@NotBlank String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public @NotBlank String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(@NotBlank String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    @NotBlank
    private String sourceAccountNumber;

    @NotBlank
    private String destinationAccountNumber;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String description;
}
