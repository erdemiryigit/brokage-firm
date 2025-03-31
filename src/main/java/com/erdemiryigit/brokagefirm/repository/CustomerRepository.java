package com.erdemiryigit.brokagefirm.repository;


import com.erdemiryigit.brokagefirm.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByName(String customerName);

    Optional<Customer> findById(UUID id);
}
