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
    @DisplayName("Test 1: Save Order")
    @org.junit.jupiter.api.Order(1)
    @Rollback(value = false)
    public void saveOrderTest() {
        Order order = Order.builder()
                .orderSide(OrderSide.BUY)
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        System.out.println(order);
        Assertions.assertThat(order.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test 2: Get Order by ID")
    @org.junit.jupiter.api.Order(2)
    public void getOrderTest() {
        Order order = orderRepository.findById(1L).orElse(null);

        System.out.println(order);
        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Test 3: Get All Orders")
    @org.junit.jupiter.api.Order(3)
    public void getAllOrdersTest() {
        List<Order> orders = orderRepository.findAll();

        System.out.println(orders);
        Assertions.assertThat(orders).isNotEmpty();
    }

    @Test
    @DisplayName("Test 4: Update Order")
    @org.junit.jupiter.api.Order(4)
    @Rollback(value = false)
    public void updateOrderTest() {
        Order order = orderRepository.findById(1L).orElse(null);
        Assertions.assertThat(order).isNotNull();

        Order updatedOrder = orderRepository.save(order);

        System.out.println(updatedOrder);
        Assertions.assertThat(updatedOrder.getPrice()).isEqualTo(52000.0);
    }

    @Test
    @DisplayName("Test 5: Delete Order")
    @org.junit.jupiter.api.Order(5)
    @Rollback(value = false)
    public void deleteOrderTest() {
        orderRepository.deleteById(1L);
        Optional<Order> deletedOrder = orderRepository.findById(1L);

        Assertions.assertThat(deletedOrder).isEmpty();
    }
}
