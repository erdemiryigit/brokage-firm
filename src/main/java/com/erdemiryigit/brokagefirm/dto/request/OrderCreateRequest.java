package com.erdemiryigit.brokagefirm.dto.request;

import com.erdemiryigit.brokagefirm.entity.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderCreateRequest(
        @NotNull(message = "Customer ID cannot be null")
        Long customerId,

        @NotBlank(message = "Ticker cannot be blank")
        String ticker,

        @NotNull(message = "Order side cannot be null")
        Order.OrderSide orderSide,

        @NotNull(message = "Size cannot be null")
        @Min(value = 1, message = "Size must be at least 1")
        BigDecimal size,

        @NotNull(message = "Price cannot be null")
        @Positive(message = "Price must be positive")
        BigDecimal price
) {}