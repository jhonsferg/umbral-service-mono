package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);

    List<Transaction> findByUserIdAndDateBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByUserIdAndDateGreaterThanEqual(UUID userId, LocalDateTime startDate);

    @Query("SELECT t.type, SUM(t.amount) FROM Transaction t WHERE t.userId = :userId GROUP BY t.type")
    List<Object[]> getBalanceSummary(@Param("userId") UUID userId);

    @Query("SELECT c.id, c.name, c.color, c.type, SUM(t.amount) " +
            "FROM Transaction t JOIN t.category c " +
            "WHERE t.userId = :userId GROUP BY c.id, c.name, c.color, c.type")
    List<Object[]> getCategorySummary(@Param("userId") UUID userId);

    @Query("SELECT t.amount, t.date, t.type FROM Transaction t " +
            "WHERE t.userId = :userId AND t.date >= :startDate")
    List<Object[]> getMonthlyTrend(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT t.categoryId, SUM(t.amount) FROM Transaction t " +
            "WHERE t.userId = :userId AND t.categoryId IN :categoryIds " +
            "AND t.date >= :startDate AND t.date <= :endDate GROUP BY t.categoryId")
    List<Object[]> getSpentByCategoryIds(@Param("userId") UUID userId,
                                         @Param("categoryIds") List<UUID> categoryIds,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}
