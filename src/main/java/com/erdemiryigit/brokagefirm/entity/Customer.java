package com.erdemiryigit.brokagefirm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id; // You can use UUID as a string or generate numeric ID

    @Column(nullable = false)
    private String name;

}
