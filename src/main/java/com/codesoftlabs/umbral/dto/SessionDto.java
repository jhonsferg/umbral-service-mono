package com.codesoftlabs.umbral.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private UUID id;
    private String deviceName;
    private String platform;
    private String userAgent;
    private String ipAddress;
    private String country;
    private String city;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime accessExpiresAt;
    private Boolean isRevoked;
    private String revokedReason;
}
