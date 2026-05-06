package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.ActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
