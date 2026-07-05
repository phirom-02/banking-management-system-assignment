package com.geekybyte.bmsgui.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.geekybyte.bmsgui.core.ApiClient;
import com.geekybyte.bmsgui.model.*;

public class TransactionApi {

    private final ApiClient client = new ApiClient();

    public TransactionDto deposit(DepositRequest request) {
        return client.post("/transactions/deposit", request, TransactionDto.class);
    }

    public TransactionDto withdraw(WithdrawalRequest request) {
        return client.post("/transactions/withdraw", request, TransactionDto.class);
    }

    public TransactionDto transfer(TransferRequest request) {
        return client.post("/transactions/transfer", request, TransactionDto.class);
    }

    public TransactionDto getByReference(String reference) {
        return client.get("/transactions/" + reference, TransactionDto.class);
    }

    public PageResponse<TransactionDto> listForAccount(Long accountId, int page, int size) {
        return client.get("/transactions/account/" + accountId + "?page=" + page + "&size=" + size + "&sort=createdAt,desc",
                new TypeReference<PageResponse<TransactionDto>>() {
                });
    }

    public PageResponse<TransactionDto> listAll(int page, int size) {
        return client.get("/transactions?page=" + page + "&size=" + size + "&sort=createdAt,desc",
                new TypeReference<PageResponse<TransactionDto>>() {
                });
    }
}
