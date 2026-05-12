package com.codesoftlabs.umbral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionDto {
    private UUID id;
    private String deviceName;
    private String platform;
    private String userAgent;
    private String ipAddress;
    private String country;
    private String city;
    private LocalDateTime lastUsedAt;
    private LocalDateTime accessExpiresAt;
    private LocalDateTime refreshExpiresAt;
    private Boolean isRevoked;
    private LocalDateTime revokedAt;
    private String revokedReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
