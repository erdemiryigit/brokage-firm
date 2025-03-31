package com.erdemiryigit.brokagefirm.dto.response;

import com.erdemiryigit.brokagefirm.entity.Order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderMatchResponse(
        UUID id,
        UUID customerId,
        String ticker,
        Order.OrderSide orderSide,
        OrderResponseStatus orderResponseStatus,
        BigDecimal size,
        BigDecimal price
) {
}