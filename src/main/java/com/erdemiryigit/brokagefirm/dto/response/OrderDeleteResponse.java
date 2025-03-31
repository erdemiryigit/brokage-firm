package com.erdemiryigit.brokagefirm.dto.response;

import com.erdemiryigit.brokagefirm.entity.Order;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderDeleteResponse(
        UUID id,
        UUID customerId,
        String ticker,
        Order.OrderSide orderSide,
        OrderResponseStatus orderResponseStatus,
        BigDecimal size,
        BigDecimal price
) {
}