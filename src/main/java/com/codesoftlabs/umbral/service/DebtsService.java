package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.dto.CreateDebtDto;
import com.codesoftlabs.umbral.dto.TransactionDto;
import com.codesoftlabs.umbral.dto.response.DebtResponseDto;
import com.codesoftlabs.umbral.entity.Category;
import com.codesoftlabs.umbral.entity.Debt;
import com.codesoftlabs.umbral.entity.Transaction;
import com.codesoftlabs.umbral.mapper.DebtMapper;
import com.codesoftlabs.umbral.mapper.TransactionMapper;
import com.codesoftlabs.umbral.repository.CategoryRepository;
import com.codesoftlabs.umbral.repository.DebtRepository;
import com.codesoftlabs.umbral.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtsService {

    private final DebtRepository debtRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final DebtMapper debtMapper;
    private final TransactionMapper transactionMapper;

    @Transactional
    public DebtResponseDto create(UUID userId, CreateDebtDto dto) {
        Debt debt = new Debt();
        debt.setUserId(userId);
        debt.setName(dto.getName());
        debt.setCreditorName(dto.getCreditor());
        debt.setAmount(dto.getTotalAmount() != null ? BigDecimal.valueOf(dto.getTotalAmount()) : BigDecimal.ZERO);
        debt.setRemainingAmount(dto.getRemainingAmount() != null ? BigDecimal.valueOf(dto.getRemainingAmount()) : BigDecimal.ZERO);
        debt.setInterestRate(dto.getInterestRate() != null ? dto.getInterestRate().floatValue() : 0f);
        debt.setCurrency(dto.getCurrencyCode() != null ? dto.getCurrencyCode() : "PEN");
        debt.setDueDate(dto.getDueDate() != null ? dto.getDueDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        debt.setStatus("ACTIVE");

        return debtMapper.toDto(debtRepository.save(debt));
    }

    public List<Map<String, Object>> findAll(UUID userId) {
        List<Debt> debts = debtRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return debts.stream().map(this::computeDebt).collect(Collectors.toList());
    }

    public DebtResponseDto findOneDto(UUID userId, UUID id) {
        return debtMapper.toDto(findOne(userId, id));
    }

    public Debt findOne(UUID userId, UUID id) {
        return debtRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Debt record not found"));
    }

    @Transactional
    public TransactionDto recordPayment(UUID userId, UUID id, BigDecimal amount, UUID accountId, String description) {
        Debt debt = findOne(userId, id);

        Category category = categoryRepository.findFirstByUserIdAndNameContainingIgnoreCase(userId, "debt")
                .orElseThrow(() -> new RuntimeException("No debt category found. Please create a category with 'debt' in the name before recording payments."));

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAccountId(accountId);
        transaction.setDebtId(id);
        transaction.setCategoryId(category.getId());
        transaction.setAmount(amount);
        transaction.setType("EXPENSE");
        transaction.setDescription(description != null ? description : "Payment for " + debt.getName());
        transaction.setDate(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        BigDecimal newRemaining = debt.getRemainingAmount().subtract(amount);
        if (newRemaining.compareTo(BigDecimal.ZERO) < 0) {
            newRemaining = BigDecimal.ZERO;
        }
        debt.setRemainingAmount(newRemaining);
        debtRepository.save(debt);

        return transactionMapper.toDto(savedTransaction);
    }

    @Transactional
    public DebtResponseDto update(UUID userId, UUID id, CreateDebtDto dto) {
        Debt debt = findOne(userId, id);

        if (dto.getName() != null) debt.setName(dto.getName());
        if (dto.getCreditor() != null) debt.setCreditorName(dto.getCreditor());
        if (dto.getTotalAmount() != null) debt.setAmount(BigDecimal.valueOf(dto.getTotalAmount()));
        if (dto.getRemainingAmount() != null) debt.setRemainingAmount(BigDecimal.valueOf(dto.getRemainingAmount()));
        if (dto.getInterestRate() != null) debt.setInterestRate(dto.getInterestRate().floatValue());
        if (dto.getCurrencyCode() != null) debt.setCurrency(dto.getCurrencyCode());
        if (dto.getDueDate() != null)
            debt.setDueDate(dto.getDueDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());

        return debtMapper.toDto(debtRepository.save(debt));
    }

    @Transactional
    public void remove(UUID userId, UUID id) {
        Debt debt = findOne(userId, id);
        debtRepository.delete(debt);
    }

    private Map<String, Object> computeDebt(Debt debt) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", debt.getId());
        map.put("name", debt.getName());
        map.put("creditor", debt.getCreditorName());
        map.put("amount", debt.getAmount());
        map.put("remainingAmount", debt.getRemainingAmount());
        map.put("interestRate", debt.getInterestRate());
        map.put("currency", debt.getCurrency());
        map.put("dueDate", debt.getDueDate());
        map.put("status", debt.getStatus());
        map.put("userId", debt.getUserId());
        map.put("createdAt", debt.getCreatedAt());
        map.put("updatedAt", debt.getUpdatedAt());

        double total = debt.getAmount() != null ? debt.getAmount().doubleValue() : 0;
        double remaining = debt.getRemainingAmount() != null ? debt.getRemainingAmount().doubleValue() : 0;

        map.put("repaidAmount", total - remaining);
        map.put("percentage", total > 0 ? Math.min(((total - remaining) / total) * 100, 100) : 0);

        return map;
    }
}
