package com.firom.bms.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@SuppressWarnings("all")
public class CustomerRequest {

    @NotBlank
    @Size(max = 150)
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(max = 30)
    private String phone;

    @NotBlank
    @Size(max = 50)
    private String nationalId;

    @Size(max = 255)
    private String address;

    @Past
    private LocalDate dateOfBirth;

    public CustomerRequest(String fullName, String email, String phone, String nationalId, String address, LocalDate dateOfBirth) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.nationalId = nationalId;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public @NotBlank @Size(max = 150) String getFullName() {
        return fullName;
    }

    public void setFullName(@NotBlank @Size(max = 150) String fullName) {
        this.fullName = fullName;
    }

    public @NotBlank @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Email String email) {
        this.email = email;
    }

    public @NotBlank @Size(max = 30) String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank @Size(max = 30) String phone) {
        this.phone = phone;
    }

    public @NotBlank @Size(max = 50) String getNationalId() {
        return nationalId;
    }

    public void setNationalId(@NotBlank @Size(max = 50) String nationalId) {
        this.nationalId = nationalId;
    }

    public @Size(max = 255) String getAddress() {
        return address;
    }

    public void setAddress(@Size(max = 255) String address) {
        this.address = address;
    }

    public @Past LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@Past LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
