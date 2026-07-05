package com.firom.bms.services;

import com.firom.bms.dto.Combo;
import com.firom.bms.dto.account.AccountComboMetadata;
import com.firom.bms.dto.account.AccountRequest;
import com.firom.bms.dto.account.AccountResponse;
import com.firom.bms.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    AccountResponse open(AccountRequest request);

    AccountResponse getById(Integer id);

    AccountResponse getByAccountNumber(String accountNumber);

    Page<AccountResponse> list(Integer customerId, AccountStatus status, Pageable pageable);

    AccountResponse updateStatus(Integer id, AccountStatus newStatus);

    List<Combo<AccountComboMetadata>> combo(Integer customerId, String status);
}
