package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, UUID> {
    List<RecurringTransaction> findByUserIdOrderByNextOccurrenceDateAsc(UUID userId);

    Optional<RecurringTransaction> findByIdAndUserId(UUID id, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);

    List<RecurringTransaction> findByStatusAndNextOccurrenceDateLessThanEqual(String status, LocalDateTime now);
}
