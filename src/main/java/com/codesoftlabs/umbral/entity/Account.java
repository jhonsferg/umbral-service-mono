package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private User owner;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String type; // SAVINGS, CHECKING, INVESTMENT, etc.

    @Column
    private String bankName;

    @Column
    private String accountNumber;

    @Builder.Default
    @Column(length = 3, nullable = false)
    private String currency = "PEN";

    @Column
    private String color;

    @Column
    private String icon;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "\"order\"", nullable = false)
    private Integer order = 0;

    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private java.math.BigDecimal balance = java.math.BigDecimal.ZERO;

    @Column(name = "bank_id")
    private UUID bankId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id", insertable = false, updatable = false)
    private Bank bank;

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
