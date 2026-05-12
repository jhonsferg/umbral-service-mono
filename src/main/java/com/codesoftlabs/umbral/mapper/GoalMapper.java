package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.response.GoalResponseDto;
import com.codesoftlabs.umbral.entity.Goal;
import org.springframework.stereotype.Component;

@Component
public class GoalMapper implements EntityMapper<Goal, GoalResponseDto> {

    @Override
    public GoalResponseDto toDto(Goal entity) {
        if (entity == null) {
            return null;
        }

        Double progressPercentage = null;
        if (entity.getTargetAmount() != null && entity.getCurrentAmount() != null) {
            progressPercentage = entity.getCurrentAmount()
                    .doubleValue() / entity.getTargetAmount().doubleValue() * 100;
        }

        return GoalResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .targetAmount(entity.getTargetAmount())
                .currentAmount(entity.getCurrentAmount())
                .targetDate(entity.getTargetDate() != null ? entity.getTargetDate().toString() : null)
                .progressPercentage(progressPercentage)
                .status(entity.getStatus())
                .description(entity.getDescription())
                .build();
    }

    @Override
    public Goal toEntity(GoalResponseDto dto) {
        if (dto == null) {
            return null;
        }

        return Goal.builder()
                .id(dto.getId())
                .name(dto.getName())
                .category(dto.getCategory())
                .targetAmount(dto.getTargetAmount())
                .currentAmount(dto.getCurrentAmount())
                .status(dto.getStatus())
                .description(dto.getDescription())
                .build();
    }
}
