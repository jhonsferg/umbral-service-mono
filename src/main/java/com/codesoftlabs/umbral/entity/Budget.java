package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Column(length = 3, nullable = false)
    private String currencyCode = "PEN";

    @Column
    private Integer month;

    @Column
    private Integer year;

    @Builder.Default
    @Column(nullable = false)
    private Integer alertThreshold = 80;

    @Builder.Default
    @Column(nullable = false)
    private Boolean rollover = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
