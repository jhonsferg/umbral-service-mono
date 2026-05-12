package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_transactions", indexes = {
        @Index(name = "idx_user_date", columnList = "user_id, date"),
        @Index(name = "idx_user_type", columnList = "user_id, type"),
        @Index(name = "idx_category_id", columnList = "category_id"),
        @Index(name = "idx_account_id", columnList = "account_id"),
        @Index(name = "idx_debt_id", columnList = "debt_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Builder.Default
    @Column(length = 3, nullable = false)
    private String currencyCode = "PEN";

    @Column(precision = 15, scale = 2)
    private BigDecimal amountBase;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 255)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private String type; // INCOME or EXPENSE

    @Column(length = 512)
    private String attachmentUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isReconciled = false;

    @Column
    private LocalDateTime reconciledAt;

    @Column(length = 100)
    private String location;

    @Column(name = "category_id")
    private UUID categoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    @Column(name = "account_id")
    private UUID accountId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @Column(name = "debt_id")
    private UUID debtId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "debt_id", insertable = false, updatable = false)
    private Debt debt;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
