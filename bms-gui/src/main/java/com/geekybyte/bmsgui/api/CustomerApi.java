package com.geekybyte.bmsgui.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.geekybyte.bmsgui.core.ApiClient;
import com.geekybyte.bmsgui.model.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomerApi {

    private final ApiClient client = new ApiClient();

    public PageResponse<CustomerDto> list(String search, int page, int size) {
        StringBuilder path = new StringBuilder("/customers?page=" + page + "&size=" + size + "&sort=id,desc");
        if (search != null && !search.isBlank()) {
            path.append("&search=").append(URLEncoder.encode(search, StandardCharsets.UTF_8));
        }
        return client.get(path.toString(), new TypeReference<PageResponse<CustomerDto>>() {
        });
    }

    public CustomerDto getById(Long id) {
        return client.get("/customers/" + id, CustomerDto.class);
    }

    public CustomerDto create(CustomerRequest request) {
        return client.post("/customers", request, CustomerDto.class);
    }

    public CustomerDto update(Long id, CustomerRequest request) {
        return client.put("/customers/" + id, request, CustomerDto.class);
    }

    public CustomerDto updateStatus(Long id, String status) {
        return client.patch("/customers/" + id + "/status?status=" + status, null, CustomerDto.class);
    }

    public void delete(Long id) {
        client.delete("/customers/" + id);
    }

    public List<ComboOption<CustomerComboMetadata>> combo(String status) {
        String path = "/customers/combo" + (status != null ? "?status=" + status : "");
        return client.get(path, new TypeReference<>() {
        });
    }
}
