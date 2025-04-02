package com.erdemiryigit.brokagefirm.specification;

import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.enums.OrderStatus;
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

    public static Specification<Order> withOrderSide(OrderSide orderSide) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("orderSide"), orderSide);
    }

    public static Specification<Order> withStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Order> withCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) {
                return cb.conjunction(); //
            }

            LocalDateTime start = startDate != null ? startDate : LocalDateTime.of(1970, 1, 1, 0, 0);
            LocalDateTime end = endDate != null ? endDate : LocalDateTime.now().plusYears(100);

            return cb.between(root.get("createDate"), start, end);
        };
    }

    public static Specification<Order> withPriceGreaterThan(BigDecimal price) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<Order> withPriceLessThan(BigDecimal price) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<Order> withPriceEquals(BigDecimal price) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("price"), price);
    }

    public static Specification<Order> withSizeEquals(BigDecimal size) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("size"), size);
    }

    public static Specification<Order> withSizeGreaterThan(BigDecimal size) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("size"), size);
    }

    public static Specification<Order> withSizeLessThan(BigDecimal size) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("size"), size);
    }

}