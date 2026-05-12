package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.response.AssetResponseDto;
import com.codesoftlabs.umbral.entity.Asset;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper implements EntityMapper<Asset, AssetResponseDto> {

    @Override
    public AssetResponseDto toDto(Asset entity) {
        if (entity == null) {
            return null;
        }

        return AssetResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .value(entity.getValue())
                .currency(entity.getCurrency())
                .description(entity.getDescription())
                .build();
    }

    @Override
    public Asset toEntity(AssetResponseDto dto) {
        if (dto == null) {
            return null;
        }

        return Asset.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .value(dto.getValue())
                .currency(dto.getCurrency())
                .description(dto.getDescription())
                .build();
    }
}
