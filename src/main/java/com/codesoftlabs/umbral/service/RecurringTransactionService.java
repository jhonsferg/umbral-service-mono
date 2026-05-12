package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.dto.CreateRecurringTransactionDto;
import com.codesoftlabs.umbral.dto.RecurringTransactionDto;
import com.codesoftlabs.umbral.dto.UpdateRecurringTransactionDto;
import com.codesoftlabs.umbral.entity.RecurringTransaction;
import com.codesoftlabs.umbral.mapper.RecurringTransactionMapper;
import com.codesoftlabs.umbral.repository.RecurringTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class RecurringTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(RecurringTransactionService.class);

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final RecurringTransactionMapper recurringTransactionMapper;

    public RecurringTransactionService(RecurringTransactionRepository recurringTransactionRepository, RecurringTransactionMapper recurringTransactionMapper) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.recurringTransactionMapper = recurringTransactionMapper;
    }

    @Transactional
    public RecurringTransactionDto create(UUID userId, CreateRecurringTransactionDto dto) {
        UUID categoryId = dto.getCategoryId();

        RecurringTransaction recurring = RecurringTransaction.builder()
                .name(dto.getDescription() != null ? dto.getDescription() : "Recurring Transaction")
                .type(dto.getType())
                .frequency(dto.getFrequency())
                .description(dto.getDescription())
                .nextOccurrenceDate(dto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .endDate(dto.getEndDate() != null
                        ? dto.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .userId(userId)
                .categoryId(categoryId)
                .status("ACTIVE")
                .autoGenerate(true)
                .build();

        return recurringTransactionMapper.toDto(recurringTransactionRepository.save(recurring));
    }

    @Transactional
    public RecurringTransactionDto update(UUID userId, UUID id, UpdateRecurringTransactionDto dto) {
        RecurringTransaction recurring = recurringTransactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Recurring transaction not found"));

        if (dto.getType() != null)
            recurring.setType(dto.getType());
        if (dto.getFrequency() != null)
            recurring.setFrequency(dto.getFrequency());
        if (dto.getDescription() != null) {
            recurring.setDescription(dto.getDescription());
            recurring.setName(dto.getDescription());
        }
        if (dto.getStartDate() != null) {
            recurring.setNextOccurrenceDate(
                    dto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (dto.getEndDate() != null) {
            recurring.setEndDate(dto.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (dto.getStatus() != null)
            recurring.setStatus(dto.getStatus());

        return recurringTransactionMapper.toDto(recurringTransactionRepository.save(recurring));
    }

    @Transactional(readOnly = true)
    public List<RecurringTransactionDto> findAll(UUID userId) {
        return recurringTransactionRepository.findByUserIdOrderByNextOccurrenceDateAsc(userId)
                .stream()
                .map(recurringTransactionMapper::toDto)
                .toList();
    }

    @Transactional
    public void remove(UUID userId, UUID id) {
        recurringTransactionRepository.deleteByIdAndUserId(id, userId);
    }

    @Scheduled(cron = "0 0 0 * * ?") // EVERY_DAY_AT_MIDNIGHT
    @Transactional
    public void processRecurringTransactions() {
        logger.info("Processing recurring transactions...");

        LocalDateTime now = LocalDateTime.now();
        List<RecurringTransaction> dueTransactions = recurringTransactionRepository
                .findByStatusAndNextOccurrenceDateLessThanEqual("ACTIVE", now);

        if (dueTransactions.isEmpty()) {
            logger.info("No recurring transactions due.");
            return;
        }

        int processed = 0;
        int failed = 0;

        for (RecurringTransaction recurring : dueTransactions) {
            try {
                LocalDateTime nextRun = calculateNextRun(now, recurring.getFrequency());
                boolean shouldDeactivate = recurring.getEndDate() != null && nextRun.isAfter(recurring.getEndDate());

                recurring.setNextOccurrenceDate(nextRun);
                if (shouldDeactivate) {
                    recurring.setStatus("ENDED");
                }

                recurringTransactionRepository.save(recurring);
                processed++;

            } catch (Exception err) {
                failed++;
                logger.error("Failed to process recurring transaction {}", recurring.getId(), err);
            }
        }

        logger.info("Recurring transactions processed: {} success, {} failed out of {} due.", processed, failed,
                dueTransactions.size());
    }

    private LocalDateTime calculateNextRun(LocalDateTime from, String frequency) {
        if (frequency == null) {
            return from.plusMonths(1);
        }

        return switch (frequency.toUpperCase()) {
            case "DAILY" -> from.plusDays(1);
            case "WEEKLY" -> from.plusWeeks(1);
            case "BIWEEKLY" -> from.plusWeeks(2);
            case "MONTHLY" -> from.plusMonths(1);
            case "QUARTERLY" -> from.plusMonths(3);
            case "YEARLY" -> from.plusYears(1);
            default -> from.plusMonths(1);
        };
    }
}