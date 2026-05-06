package com.codesoftlabs.umbral.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User information response")
public class UserResponse {

    @Schema(description = "User ID")
    private String id;

    @Schema(description = "User email")
    private String email;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Avatar URL")
    private String avatarUrl;

    @Schema(description = "Phone number")
    private String phoneNumber;

    @Schema(description = "Locale (language)")
    private String locale;

    @Schema(description = "Timezone")
    private String timezone;

    @Schema(description = "Default currency code")
    private String defaultCurrency;

    @Schema(description = "Whether user is active")
    private boolean isActive;

    @Schema(description = "Whether MFA is enabled")
    private boolean mfaEnabled;

    @Schema(description = "Last login timestamp")
    private LocalDateTime lastLogin;

    @Schema(description = "Email verification timestamp")
    private LocalDateTime emailVerifiedAt;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Account update timestamp")
    private LocalDateTime updatedAt;
}
