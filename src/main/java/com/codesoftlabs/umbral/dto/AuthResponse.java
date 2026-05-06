package com.codesoftlabs.umbral.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response with tokens")
public class AuthResponse {
    @Schema(description = "Access token")
    private String accessToken;
    @Schema(description = "Refresh token")
    private String refreshToken;
    @Schema(description = "User information")
    private UserResponse user;
    @Schema(description = "MFA required flag")
    private boolean requireMfa;
    @Schema(description = "User ID for MFA verification")
    private UUID userId;
    @Schema(description = "Response message")
    private String message;
}
