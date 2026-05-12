package com.codesoftlabs.umbral.dto;

import com.codesoftlabs.umbral.common.validation.annotations.IsUUID;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreateAccountDto {
    @NotBlank(message = "El nombre de la cuenta no puede estar en blanco.")
    @Size(min = 1, max = 100, message = "El nombre de la cuenta debe tener entre 1 y 100 caracteres.")
    private String name;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres.")
    private String description;

    @Pattern(regexp = "^[A-Z]{3}$", message = "El código de moneda debe ser un código ISO válido de 3 letras.")
    private String currency;

    @Pattern(regexp = "^(CHECKING|SAVINGS|CREDIT_CARD|INVESTMENT|LOAN|OTHER)$", message = "El tipo de cuenta debe ser válido")
    private String type;

    @DecimalMin(value = "0.0", message = "El saldo no puede ser negativo.")
    private Double balance;

    @DecimalMin(value = "0.0", message = "El límite de crédito no puede ser negativo.")
    private Double creditLimit;

    @NotNull(message = "El ID del banco no puede ser nulo")
    @NotBlank(message = "El ID del banco no puede estar vacío")
    @IsUUID(allowCompact = true, message = "El ID del banco debe ser un UUID válido")
    private String bankId;
}
