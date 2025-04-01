package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.enums.OrderStatus;
import com.erdemiryigit.brokagefirm.entity.Customer;
import com.erdemiryigit.brokagefirm.entity.Order;
import com.erdemiryigit.brokagefirm.entity.User;
import com.erdemiryigit.brokagefirm.exception.OrderNotFoundException;
import com.erdemiryigit.brokagefirm.model.CustomUserDetails;
import com.erdemiryigit.brokagefirm.repository.CustomerRepository;
import com.erdemiryigit.brokagefirm.repository.OrderRepository;
import com.erdemiryigit.brokagefirm.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest
class UserAuthenticationServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Test
    void loadUserByOrderId_whenOrderExistsAndCustomerExists_shouldReturnUserDetails() {
        String username = "testuser";

        Customer customer = new Customer();
        customer.setName(username);
        customerRepository.save(customer);

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderSide(OrderSide.BUY);
        order.setSize(BigDecimal.valueOf(10));
        order.setPrice(BigDecimal.valueOf(100));
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(java.time.LocalDateTime.now());
        order = orderRepository.save(order);

        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        userRepository.save(user);

        CustomUserDetails userDetails = (CustomUserDetails) userAuthenticationService.loadUserByOrderId(order.getId());

        Assertions.assertEquals(user.getUsername(), userDetails.getUsername());
    }

    @Test
    void loadUserByOrderId_whenOrderDoesNotExist_shouldThrowOrderNotFoundException() {
        UUID orderId = UUID.fromString("");

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> userAuthenticationService.loadUserByOrderId(orderId));
    }

    @Test
    void loadUserByOrderId_whenCustomerDoesNotExist_shouldThrowUsernameNotFoundException() {
        UUID orderId = UUID.fromString("");

        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userAuthenticationService.loadUserByOrderId(orderId));
    }

    @Test
    void loadUserByOrderId_whenUserDoesNotExist_shouldThrowUsernameNotFoundException() {
        UUID orderId = UUID.fromString("");
        String username = "testuser";

        Customer customer = new Customer();
        customer.setName(username);

        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(customer);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userAuthenticationService.loadUserByOrderId(orderId));
    }
}