package com.erdemiryigit.brokagefirm.dto.response;

import com.erdemiryigit.brokagefirm.entity.Order;

import java.math.BigDecimal;

public record OrderMatchResponse(
        Long id,
        Long customerId,
        String ticker,
        Order.OrderSide orderSide,
        OrderResponseStatus orderResponseStatus,
        BigDecimal size,
        BigDecimal price
) {
}