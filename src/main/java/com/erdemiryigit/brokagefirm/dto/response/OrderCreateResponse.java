package com.erdemiryigit.brokagefirm.dto.response;

import com.erdemiryigit.brokagefirm.entity.Order;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCreateResponse(
        Long id,
        Long customerId,
        String ticker,
        Order.OrderSide orderSide,
        OrderResponseStatus orderResponseStatus,
        BigDecimal size,
        BigDecimal price
) {
}