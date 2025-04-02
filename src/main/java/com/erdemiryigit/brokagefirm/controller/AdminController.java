package com.erdemiryigit.brokagefirm.controller;

import com.erdemiryigit.brokagefirm.config.annotations.IsAdmin;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@SecurityRequirement(name = "basicAuth")
@IsAdmin
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final OrderService orderService;


    /**
     * Matches a pending order.
     *
     * @param orderId the ID of the order to match
     * @return the response entity containing the order match response
     */
    @Parameter(name = "orderId", example = "a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9")
    @Operation(summary = "Match Order", description = "Match a pending order.")
    @PutMapping("/orders/{orderId}")
    public ResponseEntity<OrderMatchResponse> matchOrder(@PathVariable UUID orderId) {
        log.info("Admin matching order: {}", orderId);
        try {
            return ResponseEntity.ok(orderService.matchOrder(orderId));
        } catch (InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}