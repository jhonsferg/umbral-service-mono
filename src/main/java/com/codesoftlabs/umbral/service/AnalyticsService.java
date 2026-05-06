package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.entity.Transaction;
import com.codesoftlabs.umbral.repository.AccountRepository;
import com.codesoftlabs.umbral.repository.DebtRepository;
import com.codesoftlabs.umbral.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final DebtRepository debtRepository;

    public Map<String, Object> getFinancialHealth(UUID userId) {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);

        double income = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        double expenses = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        double debtPayments = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()) && t.getDebtId() != null)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        double dti = income > 0 ? (debtPayments / income) * 100 : 0;
        double savings = Math.max(0, income - expenses);
        double savingsRate = income > 0 ? (savings / income) * 100 : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("monthlyIncome", income);
        result.put("monthlyExpenses", expenses);
        result.put("monthlyDebtPayments", debtPayments);
        result.put("debtToIncomeRatio", Math.round(dti * 100.0) / 100.0);
        result.put("savingsRate", Math.round(savingsRate * 100.0) / 100.0);
        result.put("healthStatus", getHealthStatus(dti, savingsRate));

        return result;
    }

    public List<Map<String, Object>> getNetWorthTrend(UUID userId) {
        int monthsToFetch = 6;
        YearMonth currentMonth = YearMonth.now();
        YearMonth periodStartMonth = currentMonth.minusMonths(monthsToFetch - 1);
        LocalDateTime periodStart = periodStartMonth.atDay(1).atStartOfDay();

        List<Transaction> allTransactions = transactionRepository.findByUserIdAndDateGreaterThanEqual(userId, periodStart);

        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

        for (int i = 0; i < monthsToFetch; i++) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            LocalDateTime targetDate = targetMonth.atDay(1).atStartOfDay();

            String label = targetMonth.format(formatter);

            List<Transaction> transactionsSince = allTransactions.stream()
                    .filter(t -> !t.getDate().isBefore(targetDate))
                    .toList();

            double income = transactionsSince.stream()
                    .filter(t -> "INCOME".equals(t.getType()))
                    .mapToDouble(t -> t.getAmount().doubleValue())
                    .sum();

            double expense = transactionsSince.stream()
                    .filter(t -> "EXPENSE".equals(t.getType()))
                    .mapToDouble(t -> t.getAmount().doubleValue())
                    .sum();

            double value = income - expense;

            Map<String, Object> map = new HashMap<>();
            map.put("label", label);
            map.put("value", value);
            trend.add(map);
        }

        Collections.reverse(trend);
        return trend;
    }

    private String getHealthStatus(double dti, double savingsRate) {
        if (dti > 43) return "CRITICAL";
        if (dti > 36) return "WARNING";
        if (savingsRate < 10) return "NEED_IMPROVEMENT";
        if (savingsRate >= 20 && dti <= 30) return "EXCELLENT";
        return "STABLE";
    }
}
