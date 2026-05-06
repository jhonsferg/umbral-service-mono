package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_sessions", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_access_token", columnList = "access_token", unique = true),
        @Index(name = "idx_refresh_token", columnList = "refresh_token", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false, length = 512)
    private String accessToken;

    @Column(nullable = false, length = 512)
    private String refreshToken;

    @Column(length = 255)
    private String deviceName;

    @Column(length = 50)
    private String platform;

    @Column(length = 512)
    private String userAgent;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    @Column(nullable = false)
    private LocalDateTime accessExpiresAt;

    @Column(nullable = false)
    private LocalDateTime refreshExpiresAt;

    @Column
    private LocalDateTime lastUsedAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRevoked = false;

    @Column
    private LocalDateTime revokedAt;

    @Column(length = 50)
    private String revokedReason;
}
