package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Goal> findByIdAndUserId(UUID id, UUID userId);
}
