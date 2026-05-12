package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_recurring_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringTransaction extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "category_id")
    private UUID categoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String type; // INCOME or EXPENSE

    @Column(nullable = false)
    private String frequency; // DAILY, WEEKLY, BIWEEKLY, MONTHLY, QUARTERLY, YEARLY

    @Column(nullable = false)
    private LocalDateTime nextOccurrenceDate;

    @Column
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String status; // ACTIVE, PAUSED, ENDED

    @Builder.Default
    @Column(nullable = false)
    private Boolean autoGenerate = false;

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
