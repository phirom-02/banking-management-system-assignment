package com.firom.bms.services;

import com.firom.bms.dto.auth.LoginRequest;
import com.firom.bms.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
