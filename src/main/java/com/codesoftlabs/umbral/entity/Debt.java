package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_debts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Debt extends AuditableEntity {

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

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal remainingAmount;

    @Builder.Default
    @Column(length = 3, nullable = false)
    private String currency = "PEN";

    @Column(nullable = false)
    private String type; // OWED_TO_ME or I_OWE

    @Column(length = 100)
    private String creditorName;

    @Column
    private Float interestRate;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime dueDate;

    @Column
    private LocalDateTime paidOffDate;

    @Column(nullable = false)
    private String status; // ACTIVE, SETTLED, DEFAULTED

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
