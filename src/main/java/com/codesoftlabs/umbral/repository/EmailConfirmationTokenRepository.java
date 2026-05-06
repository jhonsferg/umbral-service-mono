package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, UUID> {
    Optional<EmailConfirmationToken> findByToken(String token);

    Optional<EmailConfirmationToken> findByUserIdAndEmail(UUID userId, String email);

    @Query("SELECT t FROM EmailConfirmationToken t WHERE t.token = ?1 AND t.isUsed = false")
    Optional<EmailConfirmationToken> findValidToken(String token);
}
