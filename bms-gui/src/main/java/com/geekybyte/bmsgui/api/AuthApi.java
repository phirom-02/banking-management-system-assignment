package com.geekybyte.bmsgui.api;

import com.geekybyte.bmsgui.core.ApiClient;
import com.geekybyte.bmsgui.model.LoginRequest;
import com.geekybyte.bmsgui.model.LoginResponse;

public class AuthApi {

    private final ApiClient client = new ApiClient();

    public LoginResponse login(String username, String password) {
        return client.post("/auth/login", new LoginRequest(username, password), LoginResponse.class);
    }
}
