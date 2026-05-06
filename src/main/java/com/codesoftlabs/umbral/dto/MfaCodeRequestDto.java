package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MfaCodeRequestDto {
    @NotBlank(message = "MFA code cannot be blank")
    @Pattern(regexp = "^\\d{6}$", message = "MFA code must be exactly 6 digits")
    private String code;
}
