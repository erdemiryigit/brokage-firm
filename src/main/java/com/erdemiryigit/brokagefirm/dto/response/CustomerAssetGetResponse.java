package com.erdemiryigit.brokagefirm.dto.response;

import com.erdemiryigit.brokagefirm.entity.Order;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CustomerAssetGetResponse(
        String customerName,
        String ticker,
        String description,
        BigDecimal size,
        BigDecimal usableSize
) {
}