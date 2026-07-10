package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.*;
import com.codesoftlabs.umbral.entity.Device;
import com.codesoftlabs.umbral.entity.Session;
import com.codesoftlabs.umbral.entity.User;
import com.codesoftlabs.umbral.exception.UnauthorizedException;
import com.codesoftlabs.umbral.repository.UserRepository;
import com.codesoftlabs.umbral.security.CustomUserDetails;
import com.codesoftlabs.umbral.service.AuthService;
import com.codesoftlabs.umbral.service.MfaService;
import com.codesoftlabs.umbral.service.SessionService;
import com.codesoftlabs.umbral.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "authentication", description = "User authentication and authorization")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final MfaService mfaService;
    private final SessionService sessionService;
    private final UserService userService;

    public AuthController(AuthService authService, UserRepository userRepository, MfaService mfaService, SessionService sessionService, UserService userService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.mfaService = mfaService;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns the health status of the authentication service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided registration details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto) {
        return ResponseEntity.ok(authService.register(registerDto));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user with credentials and returns authentication tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = TokenPairDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or MFA required"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request) {
        return ResponseEntity.ok(authService.login(loginDto, request));
    }

    @PostMapping("/mfa/verify")
    @Operation(summary = "Verify MFA code", description = "Verifies a multi-factor authentication code for login completion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MFA verification successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired MFA code"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> verifyMfa(@Valid @RequestBody MfaVerifyDto verifyDto, HttpServletRequest request) {
        return ResponseEntity.ok(authService.verifyMfa(verifyDto, request));
    }

    @PostMapping("/mfa")
    @Operation(summary = "Verify MFA code (frontend contract)", description = "Verifies a multi-factor authentication code for login completion - accepts userId as String")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MFA verification successful", content = @Content(schema = @Schema(implementation = TokenPairDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired MFA code"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> verifyMfaFrontend(@Valid @RequestBody MfaVerifyRequestDto verifyDto, HttpServletRequest request) {
        MfaVerifyDto dto = new MfaVerifyDto();
        dto.setUserId(UUID.fromString(verifyDto.getUserId()));
        dto.setCode(verifyDto.getCode());
        return ResponseEntity.ok(authService.verifyMfa(dto, request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh authentication tokens", description = "Uses a refresh token to obtain new access and refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "401", description = "Refresh token expired or invalid"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequestDto dto) {
        return ResponseEntity.ok(authService.refreshTokens(dto.getRefreshToken()));
    }

    @GetMapping("/confirm")
    @Operation(summary = "Confirm email address", description = "Verifies and confirms email address using a confirmation token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired confirmation token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> confirm(@RequestParam("token") @Parameter(description = "Email confirmation token") String token) {
        var result = authService.confirmEmail(token);
        authService.clearUserCache();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/confirm-email")
    @Operation(summary = "Confirm email via POST", description = "Alternative endpoint to confirm email address using a confirmation token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired confirmation token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> confirmEmailPost(@Valid @RequestBody ConfirmEmailRequestDto dto) {
        var result = authService.confirmEmail(dto.getToken());
        authService.clearUserCache();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out the authenticated user and invalidates their tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody(required = false) Map<String, String> body) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication required");
        }
        String refreshToken = body != null ? body.get("refreshToken") : null;
        authService.logout(userDetails.user().getId(), refreshToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves the profile information of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return ResponseEntity.ok(userService.getUserProfile(userDetails.user().getId()));
    }

    @PostMapping("/mfa/setup")
    @Operation(summary = "Setup multi-factor authentication", description = "Initiates MFA setup by generating a secret and QR code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MFA setup initiated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, String>> setupMfa(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication required");
        }
        User user = userDetails.user();
        String secret = mfaService.generateSecret();
        String qrCode = mfaService.generateQrCode(user.getEmail(), secret);

        user.setMfaSecret(secret);
        userRepository.save(user);

        Map<String, String> res = new HashMap<>();
        res.put("secret", secret);
        res.put("qrCode", qrCode);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/mfa/enable")
    @Operation(summary = "Enable multi-factor authentication", description = "Enables MFA for the user after verifying the setup code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MFA enabled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid MFA code or MFA not setup"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Object> enableMfa(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody MfaCodeRequestDto dto) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication required");
        }
        User user = userDetails.user();
        if (user.getMfaSecret() == null) {
            return ResponseEntity.badRequest().body("MFA not setup");
        }

        if (!mfaService.verifyCode(dto.getCode(), user.getMfaSecret())) {
            return ResponseEntity.status(401).body("Invalid MFA code");
        }

        user.setMfaEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @PostMapping("/mfa/disable")
    @Operation(summary = "Disable multi-factor authentication", description = "Disables MFA for the user after verifying their current MFA code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MFA disabled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid MFA code"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Object> disableMfa(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody MfaCodeRequestDto dto) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication required");
        }
        User user = userDetails.user();

        if (!mfaService.verifyCode(dto.getCode(), user.getMfaSecret())) {
            return ResponseEntity.status(401).body("Invalid MFA code");
        }

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @GetMapping("/sessions")
    @Operation(summary = "Get user sessions and devices", description = "Retrieves all active sessions and connected devices for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions and devices retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getSessions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication required");
        }

        UUID userId = userDetails.user().getId();

        List<Session> sessions = sessionService.getActiveSessions(userId);
        List<SessionDto> sessionDtos = sessions.stream()
                .map(s -> SessionDto.builder()
                        .id(s.getId())
                        .deviceName(s.getDeviceName())
                        .platform(s.getPlatform())
                        .userAgent(s.getUserAgent())
                        .ipAddress(s.getIpAddress())
                        .country(s.getCountry())
                        .city(s.getCity())
                        .createdAt(s.getCreatedAt())
                        .lastUsedAt(s.getLastUsedAt())
                        .accessExpiresAt(s.getAccessExpiresAt())
                        .isRevoked(s.getIsRevoked())
                        .revokedReason(s.getRevokedReason())
                        .build())
                .collect(Collectors.toList());

        List<Device> devices = sessionService.getUserDevices(userId);
        List<DeviceDto> deviceDtos = devices.stream()
                .map(d -> DeviceDto.builder()
                        .id(d.getId())
                        .deviceName(d.getDeviceName())
                        .deviceType(d.getDeviceType())
                        .operatingSystem(d.getOperatingSystem())
                        .browser(d.getBrowser())
                        .ipAddress(d.getIpAddress())
                        .country(d.getCountry())
                        .city(d.getCity())
                        .lastActivityAt(d.getLastActivityAt())
                        .isActive(d.getIsActive())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("sessions", sessionDtos);
        response.put("devices", deviceDtos);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sessions/{sessionId}/revoke")
    @Operation(summary = "Revoke a session", description = "Revokes a specific session by ID, logging out from that device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session revoked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> revokeSession(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID sessionId) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication required");
        }

        sessionService.revokeSession(sessionId, "User revoked session");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Session revoked successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/devices/{deviceId}/disable")
    @Operation(summary = "Disable a device", description = "Disables a device, preventing future logins from that device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device disabled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> disableDevice(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID deviceId) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication required");
        }

        sessionService.deactivateDevice(deviceId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Device disabled successfully");
        return ResponseEntity.ok(response);
    }
}
