package com.erdemiryigit.brokagefirm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    public enum OrderSide {
        BUY,
        SELL
    }
    public enum OrderStatus {
        PENDING,
        MATCHED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
