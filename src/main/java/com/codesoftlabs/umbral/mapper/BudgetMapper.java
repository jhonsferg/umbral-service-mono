package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.BudgetDto;
import com.codesoftlabs.umbral.entity.Budget;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper implements EntityMapper<Budget, BudgetDto> {

    @Override
    public BudgetDto toDto(Budget entity) {
        if (entity == null) {
            return null;
        }

        return BudgetDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .currencyCode(entity.getCurrencyCode())
                .month(entity.getMonth())
                .year(entity.getYear())
                .alertThreshold(entity.getAlertThreshold())
                .rollover(entity.getRollover())
                .notes(entity.getNotes())
                .categoryId(entity.getCategoryId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Budget toEntity(BudgetDto dto) {
        if (dto == null) {
            return null;
        }

        return Budget.builder()
                .id(dto.getId())
                .amount(dto.getAmount())
                .currencyCode(dto.getCurrencyCode())
                .month(dto.getMonth())
                .year(dto.getYear())
                .alertThreshold(dto.getAlertThreshold())
                .rollover(dto.getRollover())
                .notes(dto.getNotes())
                .categoryId(dto.getCategoryId())
                .build();
    }
}
