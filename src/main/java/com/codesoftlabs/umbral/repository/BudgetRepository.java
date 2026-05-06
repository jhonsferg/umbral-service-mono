package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(UUID userId, UUID categoryId, Integer month, Integer year);

    List<Budget> findByUserIdAndMonthAndYear(UUID userId, Integer month, Integer year);

    Optional<Budget> findByIdAndUserId(UUID id, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);
}
