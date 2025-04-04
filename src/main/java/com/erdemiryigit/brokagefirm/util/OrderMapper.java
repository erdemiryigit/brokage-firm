package com.erdemiryigit.brokagefirm.util;

import com.erdemiryigit.brokagefirm.enums.OrderStatus;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderDeleteResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderGetResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderResponseStatus;
import com.erdemiryigit.brokagefirm.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderMapper {
    @Named("mapOrderStatusForCreate")
    protected OrderResponseStatus mapOrderStatusForCreate(OrderStatus status) {
        return OrderStatus.PENDING.equals(status)
                ? OrderResponseStatus.SUCCESSFUL
                : OrderResponseStatus.FAILED;
    }

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "ticker", source = "asset.ticker")
    @Mapping(target = "orderResponseStatus", source = "status", qualifiedByName = "mapOrderStatusForCreate")
    public abstract OrderCreateResponse toOrderCreateResponse(Order order);

    @Named("mapOrderStatusForMatch")
    protected OrderResponseStatus mapOrderStatusForMatch(OrderStatus status) {
        return OrderStatus.MATCHED.equals(status)
                ? OrderResponseStatus.SUCCESSFUL
                : OrderResponseStatus.FAILED;
    }

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "ticker", source = "asset.ticker")
    @Mapping(target = "orderResponseStatus", source = "status", qualifiedByName = "mapOrderStatusForDelete")
    public abstract OrderDeleteResponse toOrderDeleteResponse(Order order);

    @Named("mapOrderStatusForDelete")
    protected OrderResponseStatus mapOrderStatusForDelete(OrderStatus status) {
        return OrderStatus.CANCELLED.equals(status)
                ? OrderResponseStatus.SUCCESSFUL
                : OrderResponseStatus.FAILED;
    }

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "ticker", source = "asset.ticker")
    @Mapping(target = "orderResponseStatus", source = "status", qualifiedByName = "mapOrderStatusForMatch")
    public abstract OrderMatchResponse toOrderMatchResponse(Order order);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "ticker", source = "asset.ticker")
    @Mapping(target = "orderStatus", source = "status")
    public abstract OrderGetResponse toOrderGetResponse(Order order);

    public abstract List<OrderGetResponse> toOrderGetResponseList(List<Order> orderList);

}
