package com.firom.bms.services.impl;

import com.firom.bms.dto.account.AccountRequest;
import com.firom.bms.dto.account.AccountResponse;
import com.firom.bms.enums.AccountStatus;
import com.firom.bms.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    @Override
    public AccountResponse open(AccountRequest request) {
        return null;
    }

    @Override
    public AccountResponse getById(Long id) {
        return null;
    }

    @Override
    public AccountResponse getByAccountNumber(String accountNumber) {
        return null;
    }

    @Override
    public Page<AccountResponse> list(Long customerId, AccountStatus status, Pageable pageable) {
        return null;
    }

    @Override
    public AccountResponse updateStatus(Long id, String status) {
        return null;
    }
}
