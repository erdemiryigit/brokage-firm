package com.erdemiryigit.brokagefirm.service;

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
        order.setOrderSide(Order.OrderSide.BUY);
        order.setSize(BigDecimal.valueOf(10));
        order.setPrice(BigDecimal.valueOf(100));
        order.setStatus(Order.OrderStatus.PENDING);
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
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> userAuthenticationService.loadUserByOrderId(orderId));
    }

    @Test
    void loadUserByOrderId_whenCustomerDoesNotExist_shouldThrowUsernameNotFoundException() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userAuthenticationService.loadUserByOrderId(orderId));
    }

    @Test
    void loadUserByOrderId_whenUserDoesNotExist_shouldThrowUsernameNotFoundException() {
        Long orderId = 1L;
        Long customerId = 2L;
        String username = "testuser";

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName(username);

        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(customer);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userAuthenticationService.loadUserByOrderId(orderId));
    }
}