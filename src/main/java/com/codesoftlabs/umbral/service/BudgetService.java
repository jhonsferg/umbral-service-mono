package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.dto.BudgetDto;
import com.codesoftlabs.umbral.dto.CreateBudgetDto;
import com.codesoftlabs.umbral.entity.Budget;
import com.codesoftlabs.umbral.mapper.BudgetMapper;
import com.codesoftlabs.umbral.repository.BudgetRepository;
import com.codesoftlabs.umbral.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetMapper budgetMapper;

    public BudgetService(BudgetRepository budgetRepository, TransactionRepository transactionRepository, BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.budgetMapper = budgetMapper;
    }

    @Transactional
    public BudgetDto create(UUID userId, CreateBudgetDto dto) {
        Optional<Budget> existing = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                userId, dto.getCategoryId(), dto.getMonth(), dto.getYear());

        if (existing.isPresent()) {
            throw new RuntimeException("Budget for this category and month already exists");
        }

        Budget budget = Budget.builder()
                .amount(BigDecimal.valueOf(dto.getAmount()))
                .month(dto.getMonth())
                .year(dto.getYear())
                .categoryId(dto.getCategoryId())
                .userId(userId)
                .notes(dto.getNotes())
                .alertThreshold(dto.getAlertThreshold() != null ? dto.getAlertThreshold() : 80)
                .rollover(dto.getRollover() != null ? dto.getRollover() : false)
                .build();

        return budgetMapper.toDto(budgetRepository.save(budget));
    }

    @Transactional
    public List<BudgetDto> findAll(UUID userId, Integer month, Integer year) {
        int currentMonth = month != null ? month : LocalDateTime.now().getMonthValue();
        int currentYear = year != null ? year : LocalDateTime.now().getYear();

        return budgetRepository.findByUserIdAndMonthAndYear(userId, currentMonth, currentYear)
                .stream()
                .map(budgetMapper::toDto)
                .toList();
    }

    @Transactional
    public List<Map<String, Object>> getBudgetProgress(UUID userId, Integer month, Integer year) {
        int currentMonth = month != null ? month : LocalDateTime.now().getMonthValue();
        int currentYear = year != null ? year : LocalDateTime.now().getYear();

        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, currentMonth, currentYear);
        if (budgets.isEmpty()) return Collections.emptyList();

        List<UUID> categoryIds = new ArrayList<>();
        for (Budget b : budgets) {
            categoryIds.add(b.getCategoryId());
        }

        LocalDateTime startDate = LocalDateTime.of(currentYear, currentMonth, 1, 0, 0, 0);
        LocalDateTime endDate = YearMonth.of(currentYear, currentMonth).atEndOfMonth().atTime(23, 59, 59);

        List<Object[]> spentByCategory = transactionRepository.getSpentByCategoryIds(userId, categoryIds, startDate, endDate);

        Map<String, Double> spentMap = new HashMap<>();
        for (Object[] row : spentByCategory) {
            String catId = (String) row[0];
            Double sum = ((BigDecimal) row[1]).doubleValue();
            spentMap.put(catId, sum);
        }

        int daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth();
        int currentDay = Math.min(LocalDateTime.now().getDayOfMonth(), daysInMonth);

        List<Map<String, Object>> progressList = new ArrayList<>();

        for (Budget budget : budgets) {
            double spent = spentMap.getOrDefault(budget.getCategoryId(), 0.0);
            double total = budget.getAmount().doubleValue();
            double dailyAverage = currentDay > 0 ? spent / currentDay : 0;
            double projected = dailyAverage * daysInMonth;
            boolean isAtRisk = projected > total;

            Map<String, Object> progress = new HashMap<>();
            progress.put("id", budget.getId());
            progress.put("categoryId", budget.getCategoryId());
            if (budget.getCategory() != null) {
                progress.put("category", budget.getCategory());
            }
            progress.put("amount", total);
            progress.put("month", budget.getMonth());
            progress.put("year", budget.getYear());
            progress.put("spent", spent);
            progress.put("remaining", total - spent);
            progress.put("percentage", Math.min((spent / total) * 100, 100));
            progress.put("projected", projected);
            progress.put("isAtRisk", isAtRisk);

            progressList.add(progress);
        }

        return progressList;
    }

    @Transactional
    public BudgetDto update(UUID userId, UUID id, Double amount) {
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        budget.setAmount(BigDecimal.valueOf(amount));
        Budget saved = budgetRepository.save(budget);

        return budgetMapper.toDto(saved);
    }

    @Transactional
    public void remove(UUID userId, UUID id) {
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        budgetRepository.deleteByIdAndUserId(budget.getId(), budget.getUserId());
    }
}