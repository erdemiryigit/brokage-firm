package com.erdemiryigit.brokagefirm.repository;

import com.erdemiryigit.brokagefirm.entity.Order;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

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
