package com.firom.bms.controllers;

import com.firom.bms.dto.account.AccountRequest;
import com.firom.bms.dto.account.AccountResponse;
import com.firom.bms.dto.account.AccountStatusRequest;
import com.firom.bms.enums.AccountStatus;
import com.firom.bms.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> open(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.open(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getById(id));
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getByAccountNumber(accountNumber));
    }

    @GetMapping
    public ResponseEntity<Page<AccountResponse>> list(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) AccountStatus status,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(accountService.list(customerId, status, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody AccountStatusRequest request) {
        return ResponseEntity.ok(accountService.updateStatus(id, request.getStatus()));
    }
}
