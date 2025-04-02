package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.controller.AdminController;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderResponseStatus;
import com.erdemiryigit.brokagefirm.exception.OrderNotFoundException;
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

import java.util.UUID;

@Transactional
@SpringBootTest
class UserAuthenticationServiceTest {
    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private AdminController adminController;

    @Test
    void whenLoadUserByValidUsernameThenReturnUser() {
        String username = "customer1";
        Assertions.assertEquals(username, userAuthenticationService.loadUserByUsername(username).getUsername());
    }

    @Test
    void whenLoadUserByInvalidUsernameThenThrowUsernameNotFoundException() {
        String username = "nonexistentuser";
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userAuthenticationService.loadUserByUsername(username));
    }

    @WithMockUser(username = "customer1")
    @Test
    void whenIsOrderOwnerThenReturnTrue() {
        UUID orderId = UUID.fromString("a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9");
        Assertions.assertTrue(userAuthenticationService.isOrderOwner(orderId));
    }

    @WithMockUser(username = "customer2")
    @Test
    void whenIsNotOrderOwnerThenReturnFalse() {
        UUID orderId = UUID.fromString("a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9");
        Assertions.assertFalse(userAuthenticationService.isOrderOwner(orderId));
    }

    @Test
    void whenGetCustomerIdByValidUsernameThenReturnId() {
        String username = "customer1";
        UUID expectedId = UUID.fromString("a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6"); // Replace with actual ID
        Assertions.assertEquals(expectedId, userAuthenticationService.getCustomerIdByUsername(username));
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
    void whenMatchOrderWithAdminRoleThenReturnSuccessful() {
        UUID orderId = UUID.fromString("a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9");
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