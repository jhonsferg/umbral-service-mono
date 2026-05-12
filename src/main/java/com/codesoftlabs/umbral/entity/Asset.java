package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String type; // REAL_ESTATE, VEHICLE, INVESTMENT, etc.

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal value;

    @Builder.Default
    @Column(length = 3, nullable = false)
    private String currency = "PEN";

    @Column
    private LocalDateTime acquiredDate;

    @Column
    private LocalDateTime purchaseDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    @Column
    private String location;

    @Column
    private String condition; // EXCELLENT, GOOD, FAIR, POOR

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
