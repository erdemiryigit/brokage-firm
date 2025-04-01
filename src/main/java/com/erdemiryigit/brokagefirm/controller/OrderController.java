package com.erdemiryigit.brokagefirm.controller;

import com.erdemiryigit.brokagefirm.config.annotations.IsCustomer;
import com.erdemiryigit.brokagefirm.config.annotations.IsEmployee;
import com.erdemiryigit.brokagefirm.dto.request.OrderCreateRequest;
import com.erdemiryigit.brokagefirm.dto.response.CustomerAssetGetResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderDeleteResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderGetResponse;
import com.erdemiryigit.brokagefirm.entity.Order;
import com.erdemiryigit.brokagefirm.service.OrderService;
import com.erdemiryigit.brokagefirm.service.UserAuthenticationService;
import com.erdemiryigit.brokagefirm.specification.CustomerAssetSearchCriteria;
import com.erdemiryigit.brokagefirm.specification.OrderSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// todo return response types here instead of domain object

@IsCustomer
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @IsEmployee
    @Operation(summary = "Create Order", description = "Create a new order.")
    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody @Valid OrderCreateRequest orderCreateRequest) throws InterruptedException {
        return ResponseEntity.ok(orderService.createOrder(orderCreateRequest));
    }

    @Operation(summary = "Get Orders", description = "Search for orders based on various criteria, customer and date range is mandatory.")
    @GetMapping
    public ResponseEntity<List<OrderGetResponse>> getOrders(
            @RequestParam UUID customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) Order.OrderSide orderSide,
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal size,
            @RequestParam(required = false) BigDecimal minSize,
            @RequestParam(required = false) BigDecimal maxSize) {

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

    @Operation(summary = "Delete Order", description = "Cancel a PENDING order by ID")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderDeleteResponse> deleteOrder(@PathVariable UUID orderId) throws InterruptedException {
        return ResponseEntity.ok(orderService.deleteOrder(orderId));
    }

    @Operation(summary = "List Customer Assets", description = "List all assets for a given customer with optional filters")
    @GetMapping("/customers/{customerId}/assets")
    public ResponseEntity<List<CustomerAssetGetResponse>> getCustomerAssets(
            @PathVariable UUID customerId,
            @RequestParam(required = false) String ticker,
            @RequestParam(required = false) BigDecimal size,
            @RequestBody(required = false) BigDecimal minSize,
            @RequestBody(required = false) BigDecimal maxSize,
            @RequestParam(required = false) BigDecimal usableSize,
            @RequestParam(required = false) BigDecimal minUsableSize,
            @RequestParam(required = false) BigDecimal maxUsableSize) {

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