package com.firom.bms.services;

import com.firom.bms.dto.customer.CustomerRequest;
import com.firom.bms.dto.customer.CustomerResponse;
import com.firom.bms.enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);

    CustomerResponse getById(Long id);

    Page<CustomerResponse> list(String search, Pageable pageable);

    CustomerResponse update(Long id, CustomerRequest request);

    CustomerResponse updateStatus(Long id, CustomerStatus status);

    void delete(Long id);
}
