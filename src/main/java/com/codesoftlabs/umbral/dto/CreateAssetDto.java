package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class CreateAssetDto {
    @NotBlank(message = "Asset name cannot be blank")
    @Size(min = 1, max = 100, message = "Asset name must be between 1 and 100 characters")
    private String name;

    @Size(min = 1, max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Asset type cannot be null")
    @Pattern(regexp = "^(REAL_ESTATE|VEHICLE|INVESTMENT|JEWELRY|ART|OTHER)$", message = "Asset type must be a valid value")
    private String type;

    @NotNull(message = "Asset value cannot be null")
    @DecimalMin(value = "0.0", message = "Asset value must be greater than or equal to 0")
    private BigDecimal value;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be a valid 3-letter ISO code")
    private String currency;

    private LocalDateTime acquiredDate;
    private LocalDateTime purchaseDate;

    @DecimalMin(value = "0.0", message = "Purchase price cannot be negative")
    private BigDecimal purchasePrice;

    private String location;
    private String condition;
}
