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
public class GoalResponseDto {
    private UUID id;
    private String name;
    private String category;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String targetDate;
    private Double progressPercentage;
    private String status;
    private String description;
}
