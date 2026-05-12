package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.CategoryDto;
import com.codesoftlabs.umbral.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements EntityMapper<Category, CategoryDto> {

    @Override
    public CategoryDto toDto(Category entity) {
        if (entity == null) {
            return null;
        }

        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .color(entity.getColor())
                .icon(entity.getIcon())
                .type(entity.getType())
                .order(entity.getOrder())
                .isDefault(entity.getIsDefault())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Category toEntity(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .color(dto.getColor())
                .icon(dto.getIcon())
                .type(dto.getType())
                .order(dto.getOrder())
                .isDefault(dto.getIsDefault())
                .build();
    }
}
