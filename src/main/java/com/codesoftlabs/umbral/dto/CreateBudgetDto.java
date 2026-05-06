package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreateBudgetDto {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount must be greater than or equal to 0")
    private Double amount;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be valid")
    @Max(value = 2100, message = "Year must be valid")
    private Integer year;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private String notes;

    @Min(value = 0, message = "Alert threshold must be between 0 and 100")
    @Max(value = 100, message = "Alert threshold must be between 0 and 100")
    private Integer alertThreshold;

    private Boolean rollover;
}
