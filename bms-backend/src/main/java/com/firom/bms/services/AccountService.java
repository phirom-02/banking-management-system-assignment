package com.firom.bms.services;

import com.firom.bms.dto.account.AccountRequest;
import com.firom.bms.dto.account.AccountResponse;
import com.firom.bms.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    AccountResponse open(AccountRequest request);

    AccountResponse getById(Long id);

    AccountResponse getByAccountNumber(String accountNumber);

    Page<AccountResponse> list(Long customerId, AccountStatus status, Pageable pageable);

    AccountResponse updateStatus(Long id, String status);
}
