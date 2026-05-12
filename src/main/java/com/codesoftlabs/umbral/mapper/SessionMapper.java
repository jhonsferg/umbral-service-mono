package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.SessionDto;
import com.codesoftlabs.umbral.entity.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper implements EntityMapper<Session, SessionDto> {

    @Override
    public SessionDto toDto(Session entity) {
        if (entity == null) {
            return null;
        }

        return SessionDto.builder()
                .id(entity.getId())
                .deviceName(entity.getDeviceName())
                .platform(entity.getPlatform())
                .userAgent(entity.getUserAgent())
                .ipAddress(entity.getIpAddress())
                .country(entity.getCountry())
                .city(entity.getCity())
                .lastUsedAt(entity.getLastUsedAt())
                .accessExpiresAt(entity.getAccessExpiresAt())
                .refreshExpiresAt(entity.getRefreshExpiresAt())
                .isRevoked(entity.getIsRevoked())
                .revokedAt(entity.getRevokedAt())
                .revokedReason(entity.getRevokedReason())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Session toEntity(SessionDto dto) {
        if (dto == null) {
            return null;
        }

        return Session.builder()
                .id(dto.getId())
                .deviceName(dto.getDeviceName())
                .platform(dto.getPlatform())
                .userAgent(dto.getUserAgent())
                .ipAddress(dto.getIpAddress())
                .country(dto.getCountry())
                .city(dto.getCity())
                .lastUsedAt(dto.getLastUsedAt())
                .accessExpiresAt(dto.getAccessExpiresAt())
                .refreshExpiresAt(dto.getRefreshExpiresAt())
                .isRevoked(dto.getIsRevoked())
                .revokedAt(dto.getRevokedAt())
                .revokedReason(dto.getRevokedReason())
                .build();
    }
}
