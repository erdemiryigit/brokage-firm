package com.erdemiryigit.brokagefirm.specification;

import com.erdemiryigit.brokagefirm.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderSpecification {

    public static Specification<Order> withCustomerId(UUID customerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Order> withAssetName(String assetName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("asset").get("ticker"), assetName);
    }

    public static Specification<Order> withOrderSide(Order.OrderSide orderSide) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("orderSide"), orderSide);
    }

    public static Specification<Order> withStatus(Order.OrderStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Order> withCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createDate"), startDate, endDate);
    }

    public static Specification<Order> withPriceGreaterThan(Double price) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), BigDecimal.valueOf(price));
    }

    public static Specification<Order> withPriceLessThan(Double price) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), BigDecimal.valueOf(price));
    }

    public static Specification<Order> withSizeEquals(Integer size) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("size"), BigDecimal.valueOf(size));
    }
}