package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AuditableEntity {

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

    @Column
    private String color;

    @Column
    private String icon;

    @Column(nullable = false)
    private String type; // INCOME or EXPENSE

    @Builder.Default
    @Column(name = "\"order\"", nullable = false)
    private Integer order = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDefault = false;

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
