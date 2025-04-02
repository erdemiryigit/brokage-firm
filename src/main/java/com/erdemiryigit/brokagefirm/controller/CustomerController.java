package com.erdemiryigit.brokagefirm.controller;

import com.erdemiryigit.brokagefirm.config.annotations.IsCustomer;
import com.erdemiryigit.brokagefirm.config.annotations.IsOwner;
import com.erdemiryigit.brokagefirm.dto.response.CustomerAssetGetResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderDeleteResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderGetResponse;
import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.enums.OrderStatus;
import com.erdemiryigit.brokagefirm.service.OrderService;
import com.erdemiryigit.brokagefirm.service.UserAuthenticationService;
import com.erdemiryigit.brokagefirm.specification.CustomerAssetSearchCriteria;
import com.erdemiryigit.brokagefirm.specification.OrderSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@IsCustomer
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final OrderService orderService;
    private final UserAuthenticationService userAuthenticationService;

    @IsOwner
    @Parameter(name = "orderId", example = "a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9")
    @Operation(summary = "Get Order by ID", description = "Retrieve a specific order by its ID")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderGetResponse> getOrderById(@PathVariable UUID orderId) {
        OrderGetResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @Parameter(name = "customerId", example = "a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6")
    @Parameter(name = "startDate", example = "2025-01-01T00:00:00")
    @Parameter(name = "endDate", example = "2025-12-31T23:59:59")
    @Operation(summary = "Get Orders", description = "Search for orders based on various criteria.")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderGetResponse>> getOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) OrderSide orderSide,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal size,
            @RequestParam(required = false) BigDecimal minSize,
            @RequestParam(required = false) BigDecimal maxSize) {

        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        UUID customerId = userAuthenticationService.getCustomerIdByUsername(principal.getName());

        OrderSearchCriteria criteria = OrderSearchCriteria.builder()
                .customerId(customerId)
                .assetName(assetName)
                .orderSide(orderSide)
                .status(status)
                .price(price)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .size(size)
                .minSize(minSize)
                .maxSize(maxSize)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        log.info("Searching for orders with criteria: {}", criteria);

        List<OrderGetResponse> orders = orderService.searchOrders(criteria);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(orders);
        }
    }

    @Parameter(name = "orderId", example = "a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9")
    @IsOwner
    @Operation(summary = "Delete Order", description = "Cancel your PENDING order by ID")
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<OrderDeleteResponse> deleteOrder(@PathVariable UUID orderId) throws InterruptedException {
        return ResponseEntity.ok(orderService.deleteOrder(orderId));
    }


    @Operation(summary = "List Customer Assets", description = "List all your assets with optional filters")
    @GetMapping("/assets")
    public ResponseEntity<List<CustomerAssetGetResponse>> getCustomerAssets(
            @RequestParam(required = false) String ticker,
            @RequestParam(required = false) BigDecimal size,
            @RequestParam(required = false) BigDecimal minSize,
            @RequestParam(required = false) BigDecimal maxSize,
            @RequestParam(required = false) BigDecimal usableSize,
            @RequestParam(required = false) BigDecimal minUsableSize,
            @RequestParam(required = false) BigDecimal maxUsableSize) {

        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        UUID customerId = userAuthenticationService.getCustomerIdByUsername(principal.getName());

        CustomerAssetSearchCriteria criteria = CustomerAssetSearchCriteria.builder()
                .customerId(customerId)
                .ticker(ticker)
                .size(size)
                .minSize(minSize)
                .maxSize(maxSize)
                .usableSize(usableSize)
                .minUsableSize(minUsableSize)
                .maxUsableSize(maxUsableSize)
                .build();

        List<CustomerAssetGetResponse> assets = orderService.searchAssets(criteria);

        if (assets.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(assets);
        }
    }

}