package com.erdemiryigit.brokagefirm.repository;


import com.erdemiryigit.brokagefirm.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime from, LocalDateTime to);


}
