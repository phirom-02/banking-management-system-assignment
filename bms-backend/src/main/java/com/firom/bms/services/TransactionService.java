package com.firom.bms.services;

import com.firom.bms.dto.transaction.DepositRequest;
import com.firom.bms.dto.transaction.TransactionResponse;
import com.firom.bms.dto.transaction.TransferRequest;
import com.firom.bms.dto.transaction.WithdrawalRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    TransactionResponse deposit(DepositRequest request);

    TransactionResponse withdraw(WithdrawalRequest request);

    TransactionResponse transfer(TransferRequest request);

    TransactionResponse getByReference(String reference);

    Page<TransactionResponse> listForAccount(Long accountId, Pageable pageable);

    Page<TransactionResponse> listAll(Pageable pageable);
}
