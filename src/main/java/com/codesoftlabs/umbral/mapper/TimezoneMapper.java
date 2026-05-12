package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.TimezoneDto;
import com.codesoftlabs.umbral.entity.Timezone;
import org.springframework.stereotype.Component;

@Component
public class TimezoneMapper implements EntityMapper<Timezone, TimezoneDto> {

    @Override
    public TimezoneDto toDto(Timezone entity) {
        if (entity == null) {
            return null;
        }

        return TimezoneDto.builder()
                .id(entity.getId())
                .value(entity.getValue())
                .utc(entity.getUtc())
                .build();
    }

    @Override
    public Timezone toEntity(TimezoneDto dto) {
        if (dto == null) {
            return null;
        }

        return Timezone.builder()
                .id(dto.getId())
                .value(dto.getValue())
                .utc(dto.getUtc())
                .build();
    }
}
