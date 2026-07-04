package com.firom.bms.services;

import com.firom.bms.dto.Combo;
import com.firom.bms.dto.customer.CustomerComboMetadata;
import com.firom.bms.dto.customer.CustomerRequest;
import com.firom.bms.dto.customer.CustomerResponse;
import com.firom.bms.enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);

    CustomerResponse getById(Integer id);

    Page<CustomerResponse> list(String search, Pageable pageable);

    CustomerResponse update(Integer id, CustomerRequest request);

    CustomerResponse updateStatus(Integer id, CustomerStatus status);

    void delete(Integer id);

    List<Combo<CustomerComboMetadata>> combo(String status);
}
