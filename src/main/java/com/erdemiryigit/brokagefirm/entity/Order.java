package com.erdemiryigit.brokagefirm.entity;

import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Asset asset;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;

    @Column(nullable = false)
    private BigDecimal size;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createDate;
}
