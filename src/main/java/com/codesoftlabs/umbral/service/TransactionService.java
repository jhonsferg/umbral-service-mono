package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.AccountRole;
import com.codesoftlabs.umbral.dto.CreateTransactionDto;
import com.codesoftlabs.umbral.dto.PaginatedResponseDto;
import com.codesoftlabs.umbral.dto.UpdateTransactionDto;
import com.codesoftlabs.umbral.entity.Account;
import com.codesoftlabs.umbral.entity.Category;
import com.codesoftlabs.umbral.entity.Transaction;
import com.codesoftlabs.umbral.repository.AccountRepository;
import com.codesoftlabs.umbral.repository.CategoryRepository;
import com.codesoftlabs.umbral.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "transactions:balance", key = "#userId"),
            @CacheEvict(value = "transactions:category-summary", key = "#userId"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-1M'"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-3M'"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-6M'"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-1Y'")
    })
    public Transaction create(UUID userId, CreateTransactionDto dto, MultipartFile file) {
        checkAccountAccess(userId, dto.getAccountId(), AccountRole.EDITOR);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getType().equals(dto.getType())) {
            throw new RuntimeException("Transaction type mismatch with category");
        }

        String attachmentUrl = file != null ? uploadAttachment(file) : null;

        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(dto.getAmount()))
                .date(dto.getDate() != null ? dto.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : LocalDateTime.now())
                .description(dto.getDescription())
                .notes(dto.getNotes())
                .type(dto.getType())
                .currencyCode(dto.getCurrencyCode() != null ? dto.getCurrencyCode() : "PEN")
                .attachmentUrl(attachmentUrl)
                .userId(userId)
                .categoryId(dto.getCategoryId())
                .accountId(dto.getAccountId())
                .debtId(dto.getDebtId())
                .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "transactions:one", key = "#id"),
            @CacheEvict(value = "transactions:balance", key = "#userId"),
            @CacheEvict(value = "transactions:category-summary", key = "#userId"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-1M'"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-3M'"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-6M'"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-1Y'")
    })
    public Transaction update(UUID userId, UUID id, UpdateTransactionDto dto, MultipartFile file) {
        Transaction transaction = findOne(userId, id);

        if (dto.getAccountId() != null) {
            checkAccountAccess(userId, dto.getAccountId(), AccountRole.EDITOR);
        }

        if (file != null) {
            transaction.setAttachmentUrl(uploadAttachment(file));
        }

        if (dto.getAmount() != null) transaction.setAmount(BigDecimal.valueOf(dto.getAmount()));
        if (dto.getDate() != null)
            transaction.setDate(dto.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        if (dto.getDescription() != null) transaction.setDescription(dto.getDescription());
        if (dto.getNotes() != null) transaction.setNotes(dto.getNotes());
        if (dto.getType() != null) transaction.setType(dto.getType());
        if (dto.getCurrencyCode() != null) transaction.setCurrencyCode(dto.getCurrencyCode());
        if (dto.getCategoryId() != null) transaction.setCategoryId(dto.getCategoryId());
        if (dto.getAccountId() != null) transaction.setAccountId(dto.getAccountId());
        if (dto.getDebtId() != null) transaction.setDebtId(dto.getDebtId());

        return transactionRepository.save(transaction);
    }

    public PaginatedResponseDto<Transaction> findAll(UUID userId, String type, UUID categoryId,
                                                     UUID accountId, String startDate, String endDate,
                                                     String search, int page, int limit) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("userId"), userId));

        if (StringUtils.hasText(type)) predicates.add(cb.equal(root.get("type"), type));
        if (categoryId != null) predicates.add(cb.equal(root.get("categoryId"), categoryId));
        if (accountId != null) predicates.add(cb.equal(root.get("accountId"), accountId));
        if (StringUtils.hasText(search))
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%"));

        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            predicates.add(cb.between(root.get("date"), start, end));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("date")));

        TypedQuery<Transaction> query = entityManager.createQuery(cq);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);

        List<Transaction> result = query.getResultList();

        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Transaction> countRoot = countCq.from(Transaction.class);
        countCq.select(cb.count(countRoot));
        countCq.where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countCq).getSingleResult();

        return new PaginatedResponseDto<>(result, total, page, limit, (int) Math.ceil((double) total / limit));
    }

    @Cacheable(value = "transactions:one", key = "#id")
    public Transaction findOne(UUID userId, UUID id) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "transactions:one", key = "#id"),
            @CacheEvict(value = "transactions:balance", key = "#userId"),
            @CacheEvict(value = "transactions:category-summary", key = "#userId"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-1M'"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-3M'"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-6M'"),
            @CacheEvict(value = "transactions:trend", key = "#userId + '-1Y'")
    })
    public void remove(UUID userId, UUID id) {
        findOne(userId, id);
        transactionRepository.deleteByIdAndUserId(id, userId);
    }

    @Cacheable(value = "transactions:balance", key = "#userId")
    public Map<String, Object> getBalanceSummary(UUID userId) {
        List<Object[]> summary = transactionRepository.getBalanceSummary(userId);
        double income = 0.0;
        double expense = 0.0;

        for (Object[] row : summary) {
            String type = (String) row[0];
            BigDecimal total = (BigDecimal) row[1];
            if ("INCOME".equals(type)) income = total.doubleValue();
            if ("EXPENSE".equals(type)) expense = total.doubleValue();
        }

        Map<String, Object> res = new HashMap<>();
        res.put("totalIncome", income);
        res.put("totalExpense", expense);
        res.put("balance", income - expense);
        return res;
    }

    @Cacheable(value = "transactions:category-summary", key = "#userId")
    public List<Map<String, Object>> getCategorySummary(UUID userId) {
        List<Object[]> summary = transactionRepository.getCategorySummary(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : summary) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0].toString());
            map.put("name", row[1]);
            map.put("color", row[2]);
            map.put("type", row[3].toString());
            map.put("total", ((BigDecimal) row[4]).doubleValue());

            if ((Double) map.get("total") > 0) {
                result.add(map);
            }
        }

        result.sort((a, b) -> Double.compare((Double) b.get("total"), (Double) a.get("total")));
        return result;
    }

    @Cacheable(value = "transactions:trend", key = "#userId + '-' + #range")
    public List<Map<String, Object>> getMonthlyTrend(UUID userId, String range) {
        if (range == null) range = "6M";
        int monthsToFetch = resolveMonthRange(range);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(monthsToFetch - 1).withDayOfMonth(1).withHour(0).withMinute(0);

        List<Object[]> transactions = transactionRepository.getMonthlyTrend(userId, startDate);

        List<Map<String, Object>> trend = new ArrayList<>();

        for (int i = 0; i < monthsToFetch; i++) {
            LocalDateTime d = now.minusMonths(i);
            int year = d.getYear();
            int month = d.getMonthValue();

            double income = 0.0;
            double expense = 0.0;

            for (Object[] t : transactions) {
                LocalDateTime td = (LocalDateTime) t[1];
                if (td.getYear() == year && td.getMonthValue() == month) {
                    String type = (String) t[2];
                    double amt = ((BigDecimal) t[0]).doubleValue();
                    if ("INCOME".equals(type)) income += amt;
                    else expense += amt;
                }
            }

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("label", d.getMonth().name().substring(0, 3) + " " + year);
            monthData.put("income", income);
            monthData.put("expense", expense);
            trend.add(monthData);
        }

        Collections.reverse(trend);
        return trend;
    }

    private void checkAccountAccess(UUID userId, UUID accountId, AccountRole requiredRole) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        boolean isOwner = account.getOwnerId().equals(userId);
        // Note: Members logic is simplified here. Need to expand if necessary based on Member entity.
        if (!isOwner) {
            throw new RuntimeException("Access denied to this account");
        }
    }

    private int resolveMonthRange(String range) {
        return switch (range) {
            case "1M" -> 1;
            case "3M" -> 3;
            case "1Y" -> 12;
            default -> 6;
        };
    }

    private String uploadAttachment(MultipartFile file) {
        try {
            String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename().replaceAll("\\s", "_");
            Path uploadPath = Paths.get("uploads/receipts", filename);
            Files.createDirectories(uploadPath.getParent());
            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/receipts/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process attachment", e);
        }
    }
}