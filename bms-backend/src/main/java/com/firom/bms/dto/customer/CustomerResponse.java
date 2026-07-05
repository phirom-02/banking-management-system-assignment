package com.firom.bms.dto.customer;

import com.firom.bms.dto.account.AccountResponse;
import com.firom.bms.enums.CustomerStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("all")
public class CustomerResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String nationalId;
    private String address;
    private LocalDate dateOfBirth;
    private CustomerStatus status;
    private List<AccountResponse> accounts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerResponse() {
    }

    public CustomerResponse(Integer id, String fullName, String email, String phone, String nationalId, String address, LocalDate dateOfBirth, CustomerStatus status, List<AccountResponse> accounts, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.nationalId = nationalId;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.accounts = accounts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public List<AccountResponse> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountResponse> accounts) {
        this.accounts = accounts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
