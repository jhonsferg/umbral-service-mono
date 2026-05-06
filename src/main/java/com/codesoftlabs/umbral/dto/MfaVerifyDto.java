package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class MfaVerifyDto {
    @NotBlank
    private UUID userId;

    @NotBlank
    private String code;
}
