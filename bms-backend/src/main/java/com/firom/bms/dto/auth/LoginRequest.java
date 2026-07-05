package com.firom.bms.dto.auth;

import jakarta.validation.constraints.NotBlank;

@SuppressWarnings("all")
public class LoginRequest {
    public LoginRequest(String password, String username) {
        this.password = password;
        this.username = username;
    }

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public @NotBlank String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank String username) {
        this.username = username;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }
}
