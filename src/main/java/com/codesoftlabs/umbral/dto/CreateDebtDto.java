package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
public class CreateDebtDto {
    @NotBlank(message = "Debt name cannot be blank")
    @Size(min = 1, max = 100, message = "Debt name must be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Creditor name cannot be blank")
    @Size(min = 1, max = 100, message = "Creditor name must be between 1 and 100 characters")
    private String creditor;

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", message = "Total amount must be greater than or equal to 0")
    private Double totalAmount;

    @NotNull(message = "Remaining amount cannot be null")
    @DecimalMin(value = "0.0", message = "Remaining amount must be greater than or equal to 0")
    private Double remainingAmount;

    @DecimalMin(value = "0.0", message = "Interest rate cannot be negative")
    private Double interestRate;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be a valid 3-letter ISO code")
    private String currencyCode;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    private String color;

    private Date dueDate;
    private UUID accountId;
}
