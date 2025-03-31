package com.erdemiryigit.brokagefirm.controller;

import com.erdemiryigit.brokagefirm.dto.request.OrderCreateRequest;
import com.erdemiryigit.brokagefirm.dto.OrderSearchCriteria;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.entity.Asset;
import com.erdemiryigit.brokagefirm.entity.Order;
import com.erdemiryigit.brokagefirm.service.OrderService;
import com.erdemiryigit.brokagefirm.util.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.util.List;

// todo return response types here instead of domain object

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final OrderMapper orderMapper;

    @Operation(summary = "Create Order", description = "Create a new order.")
    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody @Valid OrderCreateRequest orderCreateRequest) throws InterruptedException {
        return ResponseEntity.ok(orderService.createOrder(orderCreateRequest));
    }

    @Operation(summary = "Get Orders", description = "Search for orders based on various criteria, customer and date range is mandatory.")
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) Order.OrderSide orderSide,
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer size) {

        OrderSearchCriteria criteria = OrderSearchCriteria.builder()
                .customerId(customerId)
                .assetName(assetName)
                .orderSide(orderSide)
                .status(status)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .size(size)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<Order> orders = orderService.searchOrders(criteria);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(orders);
        }
    }

    @PreAuthorize("authentication.principal.username == @customerService.getUsernameByOrderId(#orderId) || hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete Order", description = "Cancel a PENDING order by ID")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Long orderId) throws InterruptedException {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get All Orders", description = "Retrieve all orders")
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(orders);
        }
    }

    @Operation(summary = "List Customer Assets", description = "List all assets for a given customer with optional filters")
    @GetMapping("/customers/{customerId}/assets")
    public ResponseEntity<List<Asset>> getCustomerAssets(
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
            @RequestParam(required = false) String ticker,
            @RequestParam(required = false) Order.OrderSide orderSide,
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer size) {

        List<Order> orders = null; //orderService.searchAssets(criteria);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(null);
        }
    }


}