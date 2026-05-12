package com.codesoftlabs.umbral.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDto {
    private UUID id;
    private String action;
    private String entityType;
    private UUID entityId;
    private String details;
    private LocalDateTime createdAt;
}
