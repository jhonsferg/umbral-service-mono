package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreateCategoryDto {
    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Category type cannot be null")
    @Pattern(regexp = "^(INCOME|EXPENSE|TRANSFER)$", message = "Type must be INCOME, EXPENSE, or TRANSFER")
    private String type;

    private String icon;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    private String color;

    private UUID parentId;
}
