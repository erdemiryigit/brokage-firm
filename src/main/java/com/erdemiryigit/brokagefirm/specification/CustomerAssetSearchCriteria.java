package com.erdemiryigit.brokagefirm.specification;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CustomerAssetSearchCriteria {
    private UUID customerId;
    private String ticker;
    private BigDecimal size;
    private BigDecimal minSize;
    private BigDecimal maxSize;
    private BigDecimal usableSize;
    private BigDecimal minUsableSize;
    private BigDecimal maxUsableSize;
}