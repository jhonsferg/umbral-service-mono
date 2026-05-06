package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_currencies", indexes = {
        @Index(columnList = "code", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 3, nullable = false)
    private String code;

    @Column(length = 10, nullable = false)
    private String symbol;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100)
    private String namePlural;

    @Column(nullable = false)
    @Builder.Default
    private Integer decimalPlaces = 2;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isBase = false;

    @Column(precision = 18, scale = 8, nullable = false)
    @Builder.Default
    private BigDecimal rate = BigDecimal.ONE;

    private LocalDateTime rateUpdatedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
