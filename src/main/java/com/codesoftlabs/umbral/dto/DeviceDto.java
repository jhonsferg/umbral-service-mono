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
public class DeviceDto {
    private UUID id;
    private String deviceName;
    private String deviceType;
    private String operatingSystem;
    private String browser;
    private String ipAddress;
    private String country;
    private String city;
    private LocalDateTime lastActivityAt;
    private Boolean isActive;
}
