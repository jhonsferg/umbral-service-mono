package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
public class UpdateRecurringTransactionDto {
    @DecimalMin(value = "0.0", message = "Amount must be greater than or equal to 0")
    private Double amount;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Pattern(regexp = "^(INCOME|EXPENSE|TRANSFER)$", message = "Type must be INCOME, EXPENSE, or TRANSFER")
    private String type;

    @Pattern(regexp = "^(DAILY|WEEKLY|BIWEEKLY|MONTHLY|QUARTERLY|YEARLY)$", message = "Frequency must be a valid value")
    private String frequency;

    private Date startDate;
    private UUID categoryId;
    private UUID accountId;
    private Date endDate;

    @Pattern(regexp = "^(ACTIVE|PAUSED|COMPLETED|CANCELLED)$", message = "Status must be ACTIVE, PAUSED, COMPLETED, or CANCELLED")
    private String status;
}
