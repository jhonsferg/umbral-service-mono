package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.TransactionDto;
import com.codesoftlabs.umbral.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper implements EntityMapper<Transaction, TransactionDto> {

    private final CategoryMapper categoryMapper;
    private final AccountMapper accountMapper;

    public TransactionMapper(CategoryMapper categoryMapper, AccountMapper accountMapper) {
        this.categoryMapper = categoryMapper;
        this.accountMapper = accountMapper;
    }

    @Override
    public TransactionDto toDto(Transaction entity) {
        if (entity == null) {
            return null;
        }

        return TransactionDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .currencyCode(entity.getCurrencyCode())
                .amountBase(entity.getAmountBase())
                .date(entity.getDate())
                .description(entity.getDescription())
                .notes(entity.getNotes())
                .type(entity.getType())
                .attachmentUrl(entity.getAttachmentUrl())
                .isReconciled(entity.getIsReconciled())
                .reconciledAt(entity.getReconciledAt())
                .location(entity.getLocation())
                .categoryId(entity.getCategoryId())
                .category(categoryMapper.toDto(entity.getCategory()))
                .accountId(entity.getAccountId())
                .account(accountMapper.toDto(entity.getAccount()))
                .debtId(entity.getDebtId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Transaction toEntity(TransactionDto dto) {
        if (dto == null) {
            return null;
        }

        return Transaction.builder()
                .id(dto.getId())
                .amount(dto.getAmount())
                .currencyCode(dto.getCurrencyCode())
                .amountBase(dto.getAmountBase())
                .date(dto.getDate())
                .description(dto.getDescription())
                .notes(dto.getNotes())
                .type(dto.getType())
                .attachmentUrl(dto.getAttachmentUrl())
                .isReconciled(dto.getIsReconciled())
                .reconciledAt(dto.getReconciledAt())
                .location(dto.getLocation())
                .categoryId(dto.getCategoryId())
                .accountId(dto.getAccountId())
                .debtId(dto.getDebtId())
                .build();
    }
}
