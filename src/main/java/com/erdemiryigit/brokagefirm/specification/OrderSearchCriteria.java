package com.erdemiryigit.brokagefirm.specification;

import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderSearchCriteria {
    private UUID customerId;
    private String assetName;
    private OrderSide orderSide;
    private OrderStatus status;
    private BigDecimal price;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal size;
    private BigDecimal minSize;
    private BigDecimal maxSize;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}