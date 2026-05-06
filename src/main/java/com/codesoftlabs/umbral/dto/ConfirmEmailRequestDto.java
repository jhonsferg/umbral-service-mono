package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfirmEmailRequestDto {
    @NotBlank(message = "Confirmation token cannot be blank")
    private String token;
}
