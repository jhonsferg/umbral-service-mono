package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findByRefreshTokenAndUserId(String refreshToken, UUID userId);

    List<Session> findByUserIdAndIsRevokedFalseOrderByLastUsedAtDesc(UUID userId);

    List<Session> findByUserIdAndIsRevokedFalse(UUID userId);

    Optional<Session> findByAccessToken(String accessToken);
}
