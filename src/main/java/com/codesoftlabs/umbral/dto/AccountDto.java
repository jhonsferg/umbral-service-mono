package com.codesoftlabs.umbral.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private UUID id;
    private String name;
    private String type;
    private String currency;
    private java.math.BigDecimal balance;
    private Boolean isActive;
    private Integer order;
    private BankSummaryDto bank;
    private UserSummaryDto owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
