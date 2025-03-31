package com.erdemiryigit.brokagefirm.dto;

import com.erdemiryigit.brokagefirm.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CustomerAssetSearchCriteria {
    private UUID customerId;
    private String ticker;
    private BigDecimal size;
    private BigDecimal usableSize;
}