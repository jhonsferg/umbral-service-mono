package com.codesoftlabs.umbral.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BudgetProgressDto {
    private String budgetName;
    private BigDecimal budgetLimit;
    private BigDecimal spent;
    private BigDecimal remaining;
    private Double percentageUsed;
    private String status;
    private String category;
}
