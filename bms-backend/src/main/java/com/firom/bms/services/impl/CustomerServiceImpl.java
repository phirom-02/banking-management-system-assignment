package com.firom.bms.services.impl;

import com.firom.bms.dto.Combo;
import com.firom.bms.dto.account.AccountResponse;
import com.firom.bms.dto.customer.CustomerComboMetadata;
import com.firom.bms.dto.customer.CustomerRequest;
import com.firom.bms.dto.customer.CustomerResponse;
import com.firom.bms.entity.Account;
import com.firom.bms.entity.Customer;
import com.firom.bms.enums.CustomerStatus;
import com.firom.bms.exception.DuplicateResourceException;
import com.firom.bms.exception.ResourceNotFoundException;
import com.firom.bms.repository.CustomerRepository;
import com.firom.bms.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings("all")
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("A customer with this email already exists");
        }
        if (customerRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateResourceException("A customer with this national ID already exists");
        }

        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setNationalId(request.getNationalId());
        customer.setAddress(request.getAddress());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setStatus(CustomerStatus.ACTIVE);

        customer = customerRepository.save(customer);
        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(Integer id) {
        return toResponse(findEntity(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CustomerResponse> list(String search, Pageable pageable) {
        Page<Customer> page = (search == null || search.isBlank())
                ? customerRepository.findAll(pageable)
                : customerRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNationalIdContainingIgnoreCase(
                        search, search, search, pageable);
        return page.map(this::toResponse);
    }

    @Override
    @Transactional
    public CustomerResponse update(Integer id, CustomerRequest request) {
        Customer customer = findEntity(id);

        if (!customer.getEmail().equalsIgnoreCase(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("A customer with this email already exists");
        }
        if (!customer.getNationalId().equalsIgnoreCase(request.getNationalId())
                && customerRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateResourceException("A customer with this national ID already exists");
        }

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setNationalId(request.getNationalId());
        customer.setAddress(request.getAddress());
        customer.setDateOfBirth(request.getDateOfBirth());

        return toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse updateStatus(Integer id, CustomerStatus status) {
        Customer customer = findEntity(id);
        customer.setStatus(status);
        return toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Customer customer = findEntity(id);
        customerRepository.delete(customer);
    }

    @Override
    public List<Combo<CustomerComboMetadata>> combo(String status) {
        java.util.List<Customer> customers;
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            customers = customerRepository.findAllByOrderByFullNameAsc();
        } else {
            CustomerStatus parsedStatus = CustomerStatus.valueOf(status.toUpperCase());
            customers = customerRepository.findByStatusOrderByFullNameAsc(parsedStatus);
        }
        return customers.stream().map(this::toComboOption).toList();
    }

    private Combo<CustomerComboMetadata> toComboOption(Customer c) {
        String label = c.getFullName() + " — " + c.getEmail() + " (ID " + c.getId() + ")";

        CustomerComboMetadata metadata = new CustomerComboMetadata();
        metadata.setEmail(c.getEmail());
        metadata.setNationalId(c.getNationalId());
        metadata.setStatus(c.getStatus().name());

        Combo<CustomerComboMetadata> combo = new Combo<>();
        combo.setLabel(label);
        combo.setValue(String.valueOf(c.getId()));
        combo.setMetadata(metadata);

        return combo;
    }

    private Customer findEntity(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    private CustomerResponse toResponse(Customer c) {
        List<AccountResponse> accounts = c.getAccounts() == null ? List.of() : c.getAccounts().stream()
                .map(this::toAccountResponse)
                .toList();

        CustomerResponse response = new CustomerResponse();
        response.setId(c.getId());
        response.setFullName(c.getFullName());
        response.setEmail(c.getEmail());
        response.setPhone(c.getPhone());
        response.setNationalId(c.getNationalId());
        response.setAddress(c.getAddress());
        response.setDateOfBirth(c.getDateOfBirth());
        response.setStatus(c.getStatus());
        response.setAccounts(accounts);
        response.setCreatedAt(c.getCreatedAt());
        response.setUpdatedAt(c.getUpdatedAt());

        return response;
    }

    private AccountResponse toAccountResponse(Account a) {

        AccountResponse response = new AccountResponse();
        response.setId(a.getId());
        response.setAccountNumber(a.getAccountNumber());
        response.setCustomerId(a.getCustomer().getId());
        response.setCustomerName(a.getCustomer().getFullName());
        response.setAccountType(a.getAccountType());
        response.setBalance(a.getBalance());
        response.setCurrency(a.getCurrency());
        response.setStatus(a.getStatus());
        response.setCreatedAt(a.getCreatedAt());

        return response;
    }
}
