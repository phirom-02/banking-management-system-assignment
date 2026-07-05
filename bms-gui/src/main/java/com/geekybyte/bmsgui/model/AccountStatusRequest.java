package com.geekybyte.bmsgui.model;

public class AccountStatusRequest {
    private String status;
    private String reason;

    public AccountStatusRequest() {
    }

    public AccountStatusRequest(String status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
