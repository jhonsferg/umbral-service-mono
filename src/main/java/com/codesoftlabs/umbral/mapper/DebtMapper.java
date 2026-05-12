package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.response.DebtResponseDto;
import com.codesoftlabs.umbral.entity.Debt;
import org.springframework.stereotype.Component;

@Component
public class DebtMapper implements EntityMapper<Debt, DebtResponseDto> {

    @Override
    public DebtResponseDto toDto(Debt entity) {
        if (entity == null) {
            return null;
        }

        return DebtResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .creditorName(entity.getCreditorName())
                .originalAmount(entity.getAmount())
                .remainingBalance(entity.getRemainingAmount())
                .interestRate(entity.getInterestRate() != null ? new java.math.BigDecimal(entity.getInterestRate().doubleValue()) : null)
                .status(entity.getStatus())
                .build();
    }

    @Override
    public Debt toEntity(DebtResponseDto dto) {
        if (dto == null) {
            return null;
        }

        return Debt.builder()
                .id(dto.getId())
                .name(dto.getName())
                .creditorName(dto.getCreditorName())
                .amount(dto.getOriginalAmount())
                .remainingAmount(dto.getRemainingBalance())
                .interestRate(dto.getInterestRate() != null ? dto.getInterestRate().floatValue() : null)
                .status(dto.getStatus())
                .build();
    }
}
