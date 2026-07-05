package com.geekybyte.bmsgui.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.geekybyte.bmsgui.core.ApiClient;
import com.geekybyte.bmsgui.model.*;

import java.util.List;

public class AccountApi {

    private final ApiClient client = new ApiClient();

    public PageResponse<AccountDto> list(Long customerId, String status, int page, int size) {
        StringBuilder path = new StringBuilder("/accounts?page=" + page + "&size=" + size + "&sort=id,desc");
        if (customerId != null) {
            path.append("&customerId=").append(customerId);
        }
        if (status != null && !status.isBlank()) {
            path.append("&status=").append(status);
        }
        return client.get(path.toString(), new TypeReference<PageResponse<AccountDto>>() {
        });
    }

    public AccountDto getById(Long id) {
        return client.get("/accounts/" + id, AccountDto.class);
    }

    public AccountDto getByAccountNumber(String accountNumber) {
        return client.get("/accounts/number/" + accountNumber, AccountDto.class);
    }

    public AccountDto open(AccountRequest request) {
        return client.post("/accounts", request, AccountDto.class);
    }

    public AccountDto updateStatus(Long id, String status, String reason) {
        return client.patch("/accounts/" + id + "/status", new AccountStatusRequest(status, reason), AccountDto.class);
    }

    public List<ComboOption<AccountComboMetadata>> combo(Long customerId, String status) {
        StringBuilder path = new StringBuilder("/accounts/combo?status=" + (status != null ? status : "ACTIVE"));
        if (customerId != null) {
            path.append("&customerId=").append(customerId);
        }
        return client.get(path.toString(), new TypeReference<>() {
        });
    }
}
