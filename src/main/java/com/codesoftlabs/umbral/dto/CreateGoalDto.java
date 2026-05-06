package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
public class CreateGoalDto {
    @NotBlank(message = "Goal name cannot be blank")
    @Size(min = 1, max = 100, message = "Goal name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Target amount cannot be null")
    @DecimalMin(value = "0.0", message = "Target amount must be greater than or equal to 0")
    private Double targetAmount;

    @DecimalMin(value = "0.0", message = "Current amount cannot be negative")
    private Double currentAmount;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be a valid 3-letter ISO code")
    private String currencyCode;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    private String color;

    private Date deadline;
    private UUID accountId;
}
