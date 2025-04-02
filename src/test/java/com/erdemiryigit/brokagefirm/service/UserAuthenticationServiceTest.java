package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.controller.AdminController;
import com.erdemiryigit.brokagefirm.dto.request.OrderCreateRequest;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderResponseStatus;
import com.erdemiryigit.brokagefirm.entity.Asset;
import com.erdemiryigit.brokagefirm.entity.Customer;
import com.erdemiryigit.brokagefirm.entity.CustomerAsset;
import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.exception.OrderNotFoundException;
import com.erdemiryigit.brokagefirm.repository.CustomerAssetRepository;
import com.erdemiryigit.brokagefirm.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Transactional
@SpringBootTest
class UserAuthenticationServiceTest {
    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminController adminController;

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerAssetRepository customerAssetRepository;

    @Test
    void whenLoadUserByValidUsernameThenReturnUser() {
        String username = "testcustomer";
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword("testcustomer");
        userRepository.save(customer);
        Assertions.assertEquals(username, userAuthenticationService.loadUserByUsername(username).getUsername());
    }

    @Test
    void whenLoadUserByInvalidUsernameThenThrowUsernameNotFoundException() {
        String username = "nonexistentuser";
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userAuthenticationService.loadUserByUsername(username));
    }

    @WithMockUser(username = "customer1")
    @Test
    void whenIsOrderOwnerThenReturnTrue() throws InterruptedException {
        Customer customer = new Customer();
        customer.setUsername("customer1");
        customer.setPassword("customer1");
        userRepository.save(customer);

        Asset asset = new Asset();
        asset.setTicker("TRY");

        CustomerAsset customerAsset = new CustomerAsset();
        customerAsset.setCustomer(customer);
        customerAsset.setAsset(asset);
        customerAsset.setUsableSize(BigDecimal.valueOf(5000));
        customerAsset.setSize(BigDecimal.valueOf(5000));
        customerAssetRepository.save(customerAsset);

        OrderCreateRequest createOrderRequest = new OrderCreateRequest(
                customer.getId(),
                "AAPL",
                OrderSide.BUY,
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(150)
        );

        OrderCreateResponse orderCreateResponse = orderService.createOrder(createOrderRequest);

        UUID orderId = orderCreateResponse.id();
        Assertions.assertTrue(userAuthenticationService.isOrderOwner(orderId));
    }

    @WithMockUser(username = "customer2")
    @Test
    void whenIsNotOrderOwnerThenReturnFalse() throws InterruptedException {
        Customer customer = new Customer();
        customer.setUsername("customer1");
        customer.setPassword("customer1");
        userRepository.save(customer);

        Asset asset = new Asset();
        asset.setTicker("TRY");

        CustomerAsset customerAsset = new CustomerAsset();
        customerAsset.setCustomer(customer);
        customerAsset.setAsset(asset);
        customerAsset.setUsableSize(BigDecimal.valueOf(5000));
        customerAsset.setSize(BigDecimal.valueOf(5000));
        customerAssetRepository.save(customerAsset);

        OrderCreateRequest createOrderRequest = new OrderCreateRequest(
                customer.getId(),
                "AAPL",
                OrderSide.BUY,
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(150)
        );

        OrderCreateResponse orderCreateResponse = orderService.createOrder(createOrderRequest);

        UUID orderId = orderCreateResponse.id();
        Assertions.assertFalse(userAuthenticationService.isOrderOwner(orderId));
    }

    @WithMockUser(username = "testcustomer")
    @Test
    void whenGetCustomerIdByValidUsernameThenReturnId() {
        String username = "testcustomer";
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword("testcustomer");
        customer = userRepository.save(customer);

        Assertions.assertEquals(customer.getId(), userAuthenticationService.getCustomerIdByUsername(username));
    }

    @Test
    void whenGetCustomerIdByInvalidUsernameThenThrowUsernameNotFoundException() {
        String username = "nonexistentuser";
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userAuthenticationService.getCustomerIdByUsername(username));
    }

    @WithMockUser(username = "customer1")
    @Test
    void whenIsOrderOwnerWithNonExistentOrderThenThrowOrderNotFoundException() {
        UUID nonExistentOrderId = UUID.fromString("a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e5");
        Assertions.assertThrows(OrderNotFoundException.class,
                () -> userAuthenticationService.isOrderOwner(nonExistentOrderId));
    }

    @Test
    void whenIsOrderOwnerWithNullAuthenticationThenReturnFalse() {
        SecurityContextHolder.clearContext(); // Clear authentication
        UUID orderId = UUID.fromString("a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9");
        Assertions.assertFalse(userAuthenticationService.isOrderOwner(orderId));
    }

    @Rollback
    @WithMockUser(authorities = "ADMIN")
    @Test
    void whenMatchOrderWithAdminRoleThenReturnSuccessful() throws InterruptedException {
        Customer customer = new Customer();
        customer.setUsername("customer1");
        customer.setPassword("customer1");
        userRepository.save(customer);
        Asset asset = new Asset();
        asset.setTicker("TRY");
        CustomerAsset customerAsset = new CustomerAsset();
        customerAsset.setCustomer(customer);
        customerAsset.setAsset(asset);
        customerAsset.setUsableSize(BigDecimal.valueOf(5000));
        customerAsset.setSize(BigDecimal.valueOf(5000));
        customerAssetRepository.save(customerAsset);

        OrderCreateRequest createOrderRequest = new OrderCreateRequest(
                customer.getId(),
                "AAPL",
                OrderSide.BUY,
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(150)
        );
        OrderCreateResponse orderCreateResponse = orderService.createOrder(createOrderRequest);
        UUID orderId = orderCreateResponse.id();
        ResponseEntity<OrderMatchResponse> response = adminController.matchOrder(orderId);
        Assertions.assertEquals(OrderResponseStatus.SUCCESSFUL, response.getBody().orderResponseStatus());
    }

    @WithMockUser(authorities = "EMPLOYEE")
    @Test
    void whenMatchOrderWithEmployeeRoleThenThrow() {
        UUID orderId = UUID.fromString("a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9");
        Assertions.assertThrows(AuthorizationDeniedException.class, () -> adminController.matchOrder(orderId));
    }

    @WithMockUser(authorities = "CUSTOMER")
    @Test
    void whenMatchOrderWithCustomerRoleThenThrow() {
        UUID orderId = UUID.fromString("a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9");
        Assertions.assertThrows(AuthorizationDeniedException.class, () -> adminController.matchOrder(orderId));
    }
}