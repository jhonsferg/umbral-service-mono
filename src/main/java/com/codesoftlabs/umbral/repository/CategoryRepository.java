package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);

    Optional<Category> findFirstByUserIdAndNameContainingIgnoreCase(UUID userId, String name);

    boolean existsByUserIdAndNameAndType(UUID userId, String name, String type);

    List<Category> findByUserIdAndTypeOrderByNameAsc(UUID userId, String type);

    List<Category> findByUserIdOrderByNameAsc(UUID userId);

    Optional<Category> findByIdAndUserId(UUID id, UUID userId);
}
