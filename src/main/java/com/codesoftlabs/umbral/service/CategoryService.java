package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.TransactionType;
import com.codesoftlabs.umbral.dto.CategoryDto;
import com.codesoftlabs.umbral.dto.CreateCategoryDto;
import com.codesoftlabs.umbral.dto.UpdateCategoryDto;
import com.codesoftlabs.umbral.entity.Category;
import com.codesoftlabs.umbral.mapper.CategoryMapper;
import com.codesoftlabs.umbral.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryDto create(UUID userId, CreateCategoryDto dto) {
        TransactionType transactionType = TransactionType.valueOf(dto.getType().toUpperCase());

        if (categoryRepository.existsByUserIdAndNameAndType(userId, dto.getName(), transactionType.toString())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with this name and type already exists");
        }

        Category category = new Category();
        category.setUserId(userId);
        category.setName(dto.getName());
        category.setType(dto.getType());
        category.setColor(dto.getColor());
        category.setIcon(dto.getIcon());

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public List<CategoryDto> findAll(UUID userId, String type) {
        if (type != null) {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            return categoryRepository.findByUserIdAndTypeOrderByNameAsc(userId, transactionType.toString())
                    .stream()
                    .map(categoryMapper::toDto)
                    .toList();
        }
        return categoryRepository.findByUserIdOrderByNameAsc(userId)
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Transactional
    public CategoryDto findOne(UUID userId, UUID id) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        return categoryMapper.toDto(category);
    }

    @Transactional
    public CategoryDto update(UUID userId, UUID id, UpdateCategoryDto dto) {
        Category existing = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getType() != null) existing.setType(dto.getType());
        if (dto.getColor() != null) existing.setColor(dto.getColor());
        if (dto.getIcon() != null) existing.setIcon(dto.getIcon());

        return categoryMapper.toDto(categoryRepository.save(existing));
    }

    @Transactional
    public void remove(UUID userId, UUID id) {
        Category existing = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        categoryRepository.delete(existing);
    }
}