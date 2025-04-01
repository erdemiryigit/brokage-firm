package com.erdemiryigit.brokagefirm.dto.response;

import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

// todo idleri responselardan kaldir?
@Builder
public record OrderGetResponse(
        UUID id,
        UUID customerId,
        String ticker,
        OrderSide orderSide,
        OrderStatus orderStatus,
        BigDecimal size,
        BigDecimal price
) {
}