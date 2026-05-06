package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT n FROM Notification n WHERE n.userId = ?1 AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.userId = ?1 AND n.isRead = false AND n.deletedAt IS NULL")
    Page<Notification> findUnreadByUserId(UUID userId, Pageable pageable);
}
