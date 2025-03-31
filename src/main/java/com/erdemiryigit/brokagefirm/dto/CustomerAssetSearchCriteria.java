package com.erdemiryigit.brokagefirm.dto;

import com.erdemiryigit.brokagefirm.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CustomerAssetSearchCriteria {
    private Long customerId;
    private String ticker;
    private BigDecimal size;
    private BigDecimal usableSize;
}