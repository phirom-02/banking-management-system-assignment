package com.firom.bms.services.impl;

import com.firom.bms.dto.customer.CustomerRequest;
import com.firom.bms.dto.customer.CustomerResponse;
import com.firom.bms.enums.CustomerStatus;
import com.firom.bms.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    @Override
    public CustomerResponse create(CustomerRequest request) {
        return null;
    }

    @Override
    public CustomerResponse getById(Long id) {
        return null;
    }

    @Override
    public Page<CustomerResponse> list(String search, Pageable pageable) {
        return null;
    }

    @Override
    public CustomerResponse update(Long id, CustomerRequest request) {
        return null;
    }

    @Override
    public CustomerResponse updateStatus(Long id, CustomerStatus status) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
