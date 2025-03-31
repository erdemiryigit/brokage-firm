package com.erdemiryigit.brokagefirm.repository;

import com.erdemiryigit.brokagefirm.entity.Order;
import com.erdemiryigit.brokagefirm.entity.Order.OrderSide;
import com.erdemiryigit.brokagefirm.entity.Order.OrderStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderRepositoryUnitTests {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Test 3: Get All Orders")
    @org.junit.jupiter.api.Order(3)
    public void getAllOrdersTest() {
        List<Order> orders = orderRepository.findAll();

        System.out.println(orders);
        Assertions.assertThat(orders).isNotEmpty();
    }


}
