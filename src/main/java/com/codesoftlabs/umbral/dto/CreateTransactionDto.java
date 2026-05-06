package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
public class CreateTransactionDto {
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", message = "Amount must be greater than or equal to 0")
    private Double amount;

    private Date date;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Transaction type cannot be null")
    @Pattern(regexp = "^(INCOME|EXPENSE|TRANSFER)$", message = "Transaction type must be INCOME, EXPENSE, or TRANSFER")
    private String type;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be a valid 3-letter ISO code")
    private String currencyCode;

    @NotNull(message = "Category ID cannot be null")
    private UUID categoryId;

    @NotNull(message = "Account ID cannot be null")
    private UUID accountId;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    private UUID debtId;
}
