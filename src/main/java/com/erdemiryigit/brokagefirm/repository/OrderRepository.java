package com.erdemiryigit.brokagefirm.repository;


import com.erdemiryigit.brokagefirm.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    List<Order> findByCustomerIdAndCreateDateBetween(UUID customerId, LocalDateTime from, LocalDateTime to);


}
