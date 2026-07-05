package com.geekybyte.bmsgui.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.geekybyte.bmsgui.core.ApiClient;
import com.geekybyte.bmsgui.model.LedgerEntryDto;
import com.geekybyte.bmsgui.model.PageResponse;

public class LedgerApi {

    private final ApiClient client = new ApiClient();

    public PageResponse<LedgerEntryDto> listForAccount(Long accountId, int page, int size) {
        return client.get("/ledger/account/" + accountId + "?page=" + page + "&size=" + size + "&sort=createdAt,desc",
                new TypeReference<PageResponse<LedgerEntryDto>>() {
                });
    }

    public PageResponse<LedgerEntryDto> listAll(int page, int size) {
        return client.get("/ledger?page=" + page + "&size=" + size + "&sort=createdAt,desc",
                new TypeReference<PageResponse<LedgerEntryDto>>() {
                });
    }
}
