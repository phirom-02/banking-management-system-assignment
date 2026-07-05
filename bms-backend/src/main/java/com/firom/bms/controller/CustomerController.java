package com.firom.bms.controller;

import com.firom.bms.dto.Combo;
import com.firom.bms.dto.customer.CustomerComboMetadata;
import com.firom.bms.dto.customer.CustomerRequest;
import com.firom.bms.dto.customer.CustomerResponse;
import com.firom.bms.enums.CustomerStatus;
import com.firom.bms.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(customerService.list(search, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Integer id, @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerResponse> updateStatus(@PathVariable Integer id, @RequestParam CustomerStatus status) {
        return ResponseEntity.ok(customerService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/combo")
    public ResponseEntity<List<Combo<CustomerComboMetadata>>> combo(
            @RequestParam(required = false, defaultValue = "ACTIVE") String status) {
        return ResponseEntity.ok(customerService.combo(status));
    }
}
