package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DebtRepository extends JpaRepository<Debt, UUID> {
    List<Debt> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Debt> findByIdAndUserId(UUID id, UUID userId);

    List<Debt> findByUserId(UUID userId);
}
