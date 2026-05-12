package com.codesoftlabs.umbral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto {
    private UUID id;
    private BigDecimal amount;
    private String currencyCode;
    private BigDecimal amountBase;
    private LocalDateTime date;
    private String description;
    private String notes;
    private String type;
    private String attachmentUrl;
    private Boolean isReconciled;
    private LocalDateTime reconciledAt;
    private String location;
    private UUID categoryId;
    private CategoryDto category;
    private UUID accountId;
    private AccountDto account;
    private UUID debtId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
