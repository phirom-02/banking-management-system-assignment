package com.firom.bms.services.impl;

import com.firom.bms.dto.transaction.DepositRequest;
import com.firom.bms.dto.transaction.TransactionResponse;
import com.firom.bms.dto.transaction.TransferRequest;
import com.firom.bms.dto.transaction.WithdrawalRequest;
import com.firom.bms.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    @Override
    public TransactionResponse deposit(DepositRequest request) {
        return null;
    }

    @Override
    public TransactionResponse withdraw(WithdrawalRequest request) {
        return null;
    }

    @Override
    public TransactionResponse transfer(TransferRequest request) {
        return null;
    }

    @Override
    public TransactionResponse getByReference(String reference) {
        return null;
    }

    @Override
    public Page<TransactionResponse> listForAccount(Long accountId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<TransactionResponse> listAll(Pageable pageable) {
        return null;
    }
}
