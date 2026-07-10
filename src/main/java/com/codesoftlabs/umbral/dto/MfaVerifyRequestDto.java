package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MfaVerifyRequestDto {
    @NotBlank
    private String userId;

    @NotBlank
    private String code;
}