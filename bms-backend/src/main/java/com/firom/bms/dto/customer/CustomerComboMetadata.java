package com.firom.bms.dto.customer;

@SuppressWarnings("all")
public class CustomerComboMetadata {
    private String email;
    private String nationalId;
    private String status;

    public CustomerComboMetadata() {
    }

    public CustomerComboMetadata(String email, String nationalId, String status) {
        this.email = email;
        this.nationalId = nationalId;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
