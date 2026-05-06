package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshTokenRequestDto {
    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
}
