package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.TransactionType;
import com.codesoftlabs.umbral.dto.CreateCategoryDto;
import com.codesoftlabs.umbral.dto.UpdateCategoryDto;
import com.codesoftlabs.umbral.entity.Category;
import com.codesoftlabs.umbral.repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @CacheEvict(value = "categoriesAll", allEntries = true)
    public Category create(UUID userId, CreateCategoryDto dto) {
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

        return categoryRepository.save(category);
    }

    @Cacheable(value = "categoriesAll", key = "#userId + '_' + (#type != null ? #type.toString() : 'all')")
    public List<Category> findAll(UUID userId, String type) {
        if (type != null) {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            return categoryRepository.findByUserIdAndTypeOrderByNameAsc(userId, transactionType.toString());
        }
        return categoryRepository.findByUserIdOrderByNameAsc(userId);
    }

    @Cacheable(value = "categoryOne", key = "#id")
    public Category findOne(UUID userId, UUID id) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        return category;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categoriesAll", allEntries = true),
            @CacheEvict(value = "categoryOne", key = "#id")
    })
    public Category update(UUID userId, UUID id, UpdateCategoryDto dto) {
        Category existing = findOne(userId, id);

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getType() != null) existing.setType(dto.getType());
        if (dto.getColor() != null) existing.setColor(dto.getColor());
        if (dto.getIcon() != null) existing.setIcon(dto.getIcon());

        return categoryRepository.save(existing);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categoriesAll", allEntries = true),
            @CacheEvict(value = "categoryOne", key = "#id")
    })
    public void remove(UUID userId, UUID id) {
        Category existing = findOne(userId, id);
        categoryRepository.delete(existing);
    }
}