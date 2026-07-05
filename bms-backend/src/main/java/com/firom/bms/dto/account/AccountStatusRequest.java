package com.firom.bms.dto.account;

import com.firom.bms.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings("all")
public class AccountStatusRequest {
    public @NotNull AccountStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull AccountStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @NotNull
    private AccountStatus status;
    private String reason;

    public AccountStatusRequest() {
    }

    public AccountStatusRequest(AccountStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }
}
