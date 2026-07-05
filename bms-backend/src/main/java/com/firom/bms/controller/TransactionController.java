package com.firom.bms.controller;

import com.firom.bms.dto.transaction.DepositRequest;
import com.firom.bms.dto.transaction.TransactionResponse;
import com.firom.bms.dto.transaction.TransferRequest;
import com.firom.bms.dto.transaction.WithdrawalRequest;
import com.firom.bms.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.transfer(request));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<TransactionResponse> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(transactionService.getByReference(reference));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransactionResponse>> listForAccount(
            @PathVariable Long accountId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(transactionService.listForAccount(accountId, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> listAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(transactionService.listAll(pageable));
    }
}
