package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_devices", indexes = {
        @Index(name = "idx_device_user_id", columnList = "user_id"),
        @Index(name = "idx_device_user_agent", columnList = "user_agent")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(length = 255)
    private String deviceName;

    @Column(length = 50)
    private String deviceType;

    @Column(length = 100)
    private String operatingSystem;

    @Column(length = 100)
    private String browser;

    @Column(nullable = false, length = 512)
    private String userAgent;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    @Column
    private LocalDateTime lastActivityAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}
