package com.erdemiryigit.brokagefirm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

// todo match endpointine isadmin koy,

@Entity
@Table(name = "roles")
@Data
public class Role {

    public enum RoleName {
        EMPLOYEE,
        ADMIN,
        CUSTOMER
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    @OneToOne(mappedBy = "role")
    private User user;
}
