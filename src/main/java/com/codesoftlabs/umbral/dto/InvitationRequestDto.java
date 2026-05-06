package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class InvitationRequestDto {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Account ID cannot be blank")
    private UUID accountId;

    @NotBlank(message = "Role cannot be blank")
    @Pattern(regexp = "^(OWNER|ADMIN|EDITOR|VIEWER)$", message = "Role must be one of: OWNER, ADMIN, EDITOR, VIEWER")
    private String role;
}
