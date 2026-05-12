package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.CountryDto;
import com.codesoftlabs.umbral.entity.Country;
import org.springframework.stereotype.Component;

@Component
public class CountryMapper implements EntityMapper<Country, CountryDto> {

    @Override
    public CountryDto toDto(Country entity) {
        if (entity == null) {
            return null;
        }

        return CountryDto.builder()
                .id(entity.getId())
                .timezoneId(entity.getTimezoneId())
                .name(entity.getName())
                .code(entity.getCode())
                .phone(entity.getPhone())
                .build();
    }

    @Override
    public Country toEntity(CountryDto dto) {
        if (dto == null) {
            return null;
        }

        return Country.builder()
                .id(dto.getId())
                .timezoneId(dto.getTimezoneId())
                .name(dto.getName())
                .code(dto.getCode())
                .phone(dto.getPhone())
                .build();
    }
}
