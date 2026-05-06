package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreateAccountDto {
    @NotBlank(message = "Account name cannot be blank")
    @Size(min = 1, max = 100, message = "Account name must be between 1 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be a valid 3-letter ISO code")
    private String currency;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    private String color;

    private UUID ownerId;

    @Pattern(regexp = "^(CHECKING|SAVINGS|CREDIT_CARD|INVESTMENT|LOAN|OTHER)$", message = "Account type must be valid")
    private String type;

    @DecimalMin(value = "0.0", message = "Balance cannot be negative")
    private Double balance;

    private String icon;

    @DecimalMin(value = "0.0", message = "Credit limit cannot be negative")
    private Double creditLimit;
}
