package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.BankDto;
import com.codesoftlabs.umbral.entity.Bank;
import org.springframework.stereotype.Component;

@Component
public class BankMapper implements EntityMapper<Bank, BankDto> {

    @Override
    public BankDto toDto(Bank entity) {
        if (entity == null) {
            return null;
        }

        return BankDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .shortcut(entity.getShortcut())
                .logoUrl(entity.getLogoUrl())
                .countryId(entity.getCountryId())
                .build();
    }

    @Override
    public Bank toEntity(BankDto dto) {
        if (dto == null) {
            return null;
        }

        return Bank.builder()
                .id(dto.getId())
                .name(dto.getName())
                .shortcut(dto.getShortcut())
                .logoUrl(dto.getLogoUrl())
                .countryId(dto.getCountryId())
                .build();
    }
}
