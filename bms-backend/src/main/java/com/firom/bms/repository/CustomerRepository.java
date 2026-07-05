package com.firom.bms.repository;

import com.firom.bms.entity.Customer;
import com.firom.bms.enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    Optional<Customer> findByEmail(String email);

    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);

    Page<Customer> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNationalIdContainingIgnoreCase(
            String name, String email, String nationalId, Pageable pageable);

    long countByStatus(CustomerStatus status);

    List<Customer> findByStatusOrderByFullNameAsc(CustomerStatus status);

    List<Customer> findAllByOrderByFullNameAsc();
}
