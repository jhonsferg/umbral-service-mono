package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DebtPaymentRequestDto {
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", message = "Amount must be greater than or equal to 0")
    private Double amount;

    @NotBlank(message = "Account ID cannot be blank")
    private String accountId;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;
}
