package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String description;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal targetAmount;

    @Builder.Default
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(length = 3, nullable = false)
    private String currency = "PEN";

    @Column
    private String category;

    @Column
    private String icon;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime targetDate;

    @Column(nullable = false)
    private String status; // ACTIVE, COMPLETED, CANCELLED

    @Column
    private Integer priority; // 1=Low, 2=Medium, 3=High

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
