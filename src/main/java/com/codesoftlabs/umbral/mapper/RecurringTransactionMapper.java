package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.RecurringTransactionDto;
import com.codesoftlabs.umbral.entity.RecurringTransaction;
import org.springframework.stereotype.Component;

@Component
public class RecurringTransactionMapper implements EntityMapper<RecurringTransaction, RecurringTransactionDto> {

    private final CategoryMapper categoryMapper;

    public RecurringTransactionMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public RecurringTransactionDto toDto(RecurringTransaction entity) {
        if (entity == null) {
            return null;
        }

        return RecurringTransactionDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .type(entity.getType())
                .frequency(entity.getFrequency())
                .nextOccurrenceDate(entity.getNextOccurrenceDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .autoGenerate(entity.getAutoGenerate())
                .categoryId(entity.getCategoryId())
                .category(categoryMapper.toDto(entity.getCategory()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public RecurringTransaction toEntity(RecurringTransactionDto dto) {
        if (dto == null) {
            return null;
        }

        return RecurringTransaction.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .type(dto.getType())
                .frequency(dto.getFrequency())
                .nextOccurrenceDate(dto.getNextOccurrenceDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus())
                .autoGenerate(dto.getAutoGenerate())
                .categoryId(dto.getCategoryId())
                .build();
    }
}
