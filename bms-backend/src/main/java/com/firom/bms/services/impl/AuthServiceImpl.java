package com.firom.bms.services.impl;

import com.firom.bms.dto.auth.LoginRequest;
import com.firom.bms.dto.auth.LoginResponse;
import com.firom.bms.entity.Admin;
import com.firom.bms.repository.AdminRepository;
import com.firom.bms.security.JwtUtil;
import com.firom.bms.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@SuppressWarnings("")
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("Admin not found after authentication"));

        String token = jwtUtil.generateToken(admin.getUsername(), admin.getRole().name(), admin.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setUsername(admin.getUsername());
        response.setRole(admin.getRole().name());
        response.setExpiresInMs(expirationMs);
        return response;
    }
}
