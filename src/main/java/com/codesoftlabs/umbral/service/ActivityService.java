package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.dto.ActivityLogDto;
import com.codesoftlabs.umbral.entity.ActivityLog;
import com.codesoftlabs.umbral.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public ActivityLogDto log(UUID userId, String action, String entityType, UUID entityId, String description, Map<String, Object> metadata) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUserId(userId);
        activityLog.setAction(action);
        activityLog.setEntityType(entityType);
        activityLog.setEntityId(entityId);
        activityLog.setDetails(description);

        return toDto(activityLogRepository.save(activityLog));
    }

    public List<ActivityLogDto> findAll(UUID userId, int limit) {
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit))
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ActivityLogDto toDto(ActivityLog entity) {
        return ActivityLogDto.builder()
                .id(entity.getId())
                .action(entity.getAction())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .details(entity.getDetails())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
