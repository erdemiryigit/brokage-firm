package com.erdemiryigit.brokagefirm.dto;

import com.erdemiryigit.brokagefirm.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderSearchCriteria {
    private Long customerId;
    private String assetName;
    private Order.OrderSide orderSide;
    private Order.OrderStatus status;
    private Double minPrice;
    private Double maxPrice;
    private Integer size;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}