package com.erdemiryigit.brokagefirm.controller;

import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.enums.OrderStatus;
import com.erdemiryigit.brokagefirm.config.annotations.IsEmployee;
import com.erdemiryigit.brokagefirm.dto.request.OrderCreateRequest;
import com.erdemiryigit.brokagefirm.dto.response.CustomerAssetGetResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderDeleteResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderGetResponse;
import com.erdemiryigit.brokagefirm.service.OrderService;
import com.erdemiryigit.brokagefirm.specification.CustomerAssetSearchCriteria;
import com.erdemiryigit.brokagefirm.specification.OrderSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

@IsEmployee
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Buy Order Example",
                                    summary = "Example of a buy order request",
                                    value = """
                                            {
                                              "customerId": "a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6",
                                              "ticker": "AAPL",
                                              "orderSide": "BUY",
                                              "size": 10,
                                              "price": 150.25
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Sell Order Example",
                                    summary = "Example of a sell order request",
                                    value = """
                                            {
                                              "customerId": "a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6",
                                              "ticker": "EREGL",
                                              "orderSide": "SELL",
                                              "size": 5,
                                              "price": 300.75
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Insufficient Asset Sell Order Example",
                                    summary = "Example of a sell order request with insufficient asset",
                                    value = """
                                            {
                                              "customerId": "a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6",
                                              "ticker": "EREGL",
                                              "orderSide": "SELL",
                                              "size": 999,
                                              "price": 300.75
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Insufficient Funds Buy Order Example",
                                    summary = "Example of a buy order request with insufficient funds",
                                    value = """
                                            {
                                              "customerId": "a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6",
                                              "ticker": "EREGL",
                                              "orderSide": "BUY",
                                              "size": 999,
                                              "price": 999.75
                                            }
                                            """
                            )
                    }
            )
    )
    @Operation(summary = "Create Order", description = "Create a new order.")
    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody @Valid OrderCreateRequest orderCreateRequest) throws InterruptedException {
        return ResponseEntity.ok(orderService.createOrder(orderCreateRequest));
    }

    @Parameter(name = "customerId", example = "a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6")
    @Parameter(name = "startDate", example = "2025-01-01T00:00:00")
    @Parameter(name = "endDate", example = "2025-12-31T23:59:59")
    @Operation(summary = "Get Orders", description = "Search for orders based on various criteria, customer and date range is mandatory.")
    @GetMapping
    public ResponseEntity<List<OrderGetResponse>> getOrders(
            @RequestParam UUID customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) OrderSide orderSide,
            @RequestParam(required = false) OrderStatus status,
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

    @Parameter(
            name = "orderId",
            description = "Order ID's to be deleted",
            examples = {
                    @ExampleObject(
                            name = "PENDING Order",
                            summary = "A PENDING order that can be cancelled",
                            value = "f4f5f6f7-a4a5-b4b5-c4c5-d4d5d6d7d8d9"
                    ),
                    @ExampleObject(
                            name = "CANCELLED Order",
                            summary = "A CANCELLED order that can NOT be cancelled",
                            value = "d4d5d6d7-e4e5-f4f5-a4a5-b4b5b6b7b8b9"
                    )
            }
    )
    @Operation(summary = "Delete Order", description = "Cancel a PENDING order by ID")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderDeleteResponse> deleteOrder(@PathVariable UUID orderId) throws InterruptedException {
        return ResponseEntity.ok(orderService.deleteOrder(orderId));
    }

    @Parameter(name = "customerId", example = "a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6")
    @Operation(summary = "List Customer Assets", description = "List all assets for a given customer with optional filters")
    @GetMapping("/customers/{customerId}/assets")
    public ResponseEntity<List<CustomerAssetGetResponse>> getCustomerAssets(
            @PathVariable UUID customerId,
            @RequestParam(required = false) String ticker,
            @RequestParam(required = false) BigDecimal size,
            @RequestParam(required = false) BigDecimal minSize,
            @RequestParam(required = false) BigDecimal maxSize,
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

    @Parameter(name = "orderId", example = "a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9")
    @Operation(summary = "Get Order by ID", description = "Retrieve a specific order by its ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderGetResponse> getOrderById(@PathVariable UUID orderId) {
        OrderGetResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

}