package com.firom.bms.services.impl;

import com.firom.bms.dto.auth.LoginRequest;
import com.firom.bms.dto.auth.LoginResponse;
import com.firom.bms.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Override
    public LoginResponse login(LoginRequest request) {
        return null;
    }
}
