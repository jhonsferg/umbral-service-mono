package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.NotificationDto;
import com.codesoftlabs.umbral.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements EntityMapper<Notification, NotificationDto> {

    @Override
    public NotificationDto toDto(Notification entity) {
        if (entity == null) {
            return null;
        }

        return NotificationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(entity.getType())
                .relatedEntityType(entity.getRelatedEntityType())
                .relatedEntityId(entity.getRelatedEntityId())
                .isRead(entity.getIsRead())
                .readAt(entity.getReadAt())
                .isArchived(entity.getIsArchived())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Notification toEntity(NotificationDto dto) {
        if (dto == null) {
            return null;
        }

        return Notification.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType())
                .relatedEntityType(dto.getRelatedEntityType())
                .relatedEntityId(dto.getRelatedEntityId())
                .isRead(dto.getIsRead())
                .readAt(dto.getReadAt())
                .isArchived(dto.getIsArchived())
                .build();
    }
}
