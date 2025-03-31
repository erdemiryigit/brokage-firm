package com.erdemiryigit.brokagefirm.specification;

import com.erdemiryigit.brokagefirm.entity.CustomerAsset;
import com.erdemiryigit.brokagefirm.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerAssetSpecification {

    public static Specification<CustomerAsset> withCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<CustomerAsset> withAssetId(String ticker) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("asset").get("ticker"), ticker);
    }

    public static Specification<CustomerAsset> withSizeGreaterThan(BigDecimal size) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("size"), size);
    }

    public static Specification<CustomerAsset> withSizeLessThan(BigDecimal size) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("size"), size);
    }

    public Specification<CustomerAsset> withSizeEquals(BigDecimal size) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("size"), size);
    }

    public static Specification<CustomerAsset> withUsableSizeGreaterThan(BigDecimal usableSize) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("usableSize"), usableSize);
    }

    public static Specification<CustomerAsset> withUsableSizeLessThan(BigDecimal usableSize) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("usableSize"), usableSize);
    }

    public Specification<CustomerAsset> withUsableSizeEquals(BigDecimal usableSize) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("usableSize"), usableSize);
    }
}