package com.erdemiryigit.brokagefirm.dto.response;

import com.erdemiryigit.brokagefirm.entity.Order;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

// todo idleri responselardan kaldir?
@Builder
public record OrderGetResponse(
        UUID id,
        UUID customerId,
        String ticker,
        Order.OrderSide orderSide,
        Order.OrderStatus orderStatus,
        BigDecimal size,
        BigDecimal price
) {
}