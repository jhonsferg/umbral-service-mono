package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.AccountRole;
import com.codesoftlabs.umbral.dto.CreateTransactionDto;
import com.codesoftlabs.umbral.dto.PaginatedResponseDto;
import com.codesoftlabs.umbral.dto.TransactionDto;
import com.codesoftlabs.umbral.dto.UpdateTransactionDto;
import com.codesoftlabs.umbral.entity.Account;
import com.codesoftlabs.umbral.entity.Category;
import com.codesoftlabs.umbral.entity.Transaction;
import com.codesoftlabs.umbral.mapper.TransactionMapper;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class TransactionService {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public TransactionDto create(UUID userId, CreateTransactionDto dto, MultipartFile file) throws ParseException {
        checkAccountAccess(userId, dto.getAccountId(), AccountRole.EDITOR);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getType().equals(dto.getType())) {
            throw new RuntimeException("Transaction type mismatch with category");
        }

        String attachmentUrl = file != null ? uploadAttachment(file) : null;
        Date transactionDate = this.formatter.parse(dto.getDate());
        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(dto.getAmount()))
                .date(dto.getDate() != null ? transactionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : LocalDateTime.now())
                .description(dto.getDescription())
                .notes("")
                .type(dto.getType())
                .currencyCode("PEN")
                .attachmentUrl(attachmentUrl)
                .userId(userId)
                .categoryId(dto.getCategoryId())
                .accountId(dto.getAccountId())
                .debtId(null)
                .build();
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionDto update(UUID userId, UUID id, UpdateTransactionDto dto, MultipartFile file) throws ParseException {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (dto.getAccountId() != null) {
            checkAccountAccess(userId, dto.getAccountId(), AccountRole.EDITOR);
        }

        if (file != null) {
            transaction.setAttachmentUrl(uploadAttachment(file));
        }

        Date transactionDate = this.formatter.parse(dto.getDate());
        if (dto.getAmount() != null) transaction.setAmount(BigDecimal.valueOf(dto.getAmount()));
        if (dto.getDate() != null) transaction.setDate(transactionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        if (dto.getDescription() != null) transaction.setDescription(dto.getDescription());
        if (dto.getNotes() != null) transaction.setNotes(dto.getNotes());
        if (dto.getType() != null) transaction.setType(dto.getType());
        if (dto.getCategoryId() != null) transaction.setCategoryId(dto.getCategoryId());
        if (dto.getAccountId() != null) transaction.setAccountId(dto.getAccountId());

        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<TransactionDto> findAll(UUID userId, String type, UUID categoryId,
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

        List<TransactionDto> dtoList = result.stream()
                .map(transactionMapper::toDto)
                .toList();

        return new PaginatedResponseDto<>(dtoList, total, page, limit, (int) Math.ceil((double) total / limit));
    }

    @Transactional(readOnly = true)
    public TransactionDto findOne(UUID userId, UUID id) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return transactionMapper.toDto(transaction);
    }

    @Transactional
    public void remove(UUID userId, UUID id) {
        transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transactionRepository.deleteByIdAndUserId(id, userId);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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