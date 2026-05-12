package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class UpdateTransactionDto {
    @Min(value = 0, message = "Amount must be greater than or equal to 0")
    private Double amount;

    private String date;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Pattern(regexp = "^(INCOME|EXPENSE|TRANSFER)$", message = "Transaction type must be INCOME, EXPENSE, or TRANSFER")
    private String type;

    @NotNull(message = "Category ID cannot be null")
    private UUID categoryId;

    @NotNull(message = "Account ID cannot be null")
    private UUID accountId;
}