package com.firom.bms.controller;

import com.firom.bms.dto.ledger.LedgerEntryResponse;
import com.firom.bms.services.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<LedgerEntryResponse>> forAccount(
            @PathVariable Long accountId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ledgerService.listForAccount(accountId, pageable));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Page<LedgerEntryResponse>> forTransaction(
            @PathVariable Long transactionId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ledgerService.listForTransaction(transactionId, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<LedgerEntryResponse>> all(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ledgerService.listAll(pageable));
    }
}
