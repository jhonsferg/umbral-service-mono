package com.codesoftlabs.umbral.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebtResponseDto {
    private UUID id;
    private String name;
    private String creditorName;
    private BigDecimal originalAmount;
    private BigDecimal remainingBalance;
    private BigDecimal minimumPayment;
    private BigDecimal interestRate;
    private String status;
    private String dueDateInfo;
}
