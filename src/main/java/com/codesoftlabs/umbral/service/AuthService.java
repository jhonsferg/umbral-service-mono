package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.beans.JwtBean;
import com.codesoftlabs.umbral.beans.MailBean;
import com.codesoftlabs.umbral.common.enums.EmailTemplateType;
import com.codesoftlabs.umbral.dto.LoginDto;
import com.codesoftlabs.umbral.dto.MfaVerifyDto;
import com.codesoftlabs.umbral.dto.RegisterDto;
import com.codesoftlabs.umbral.dto.TokenPairDto;
import com.codesoftlabs.umbral.dto.UserDto;
import com.codesoftlabs.umbral.entity.EmailConfirmationToken;
import com.codesoftlabs.umbral.entity.Session;
import com.codesoftlabs.umbral.entity.User;
import com.codesoftlabs.umbral.exception.BadRequestException;
import com.codesoftlabs.umbral.exception.ForbiddenException;
import com.codesoftlabs.umbral.exception.UnauthorizedException;
import com.codesoftlabs.umbral.repository.EmailConfirmationTokenRepository;
import com.codesoftlabs.umbral.repository.SessionRepository;
import com.codesoftlabs.umbral.repository.UserRepository;
import com.codesoftlabs.umbral.security.JwtProvider;
import com.codesoftlabs.umbral.service.DeviceInfoService.DeviceInfo;
import com.codesoftlabs.umbral.util.TimeUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MailService mailService;
    private final MfaService mfaService;
    private final SessionService sessionService;
    private final DeviceInfoService deviceInfoService;
    private final JwtBean jwtBean;
    private final MailBean mailBean;

    /**
     * Register a new user account
     */
    @CacheEvict(value = "userCache", allEntries = true)
    public Map<String, String> register(RegisterDto registerDto) {
        log.info("Registering new user: {}", registerDto.getEmail());
        log.info("Using SMTP Web URL: {}", this.mailBean.getSmtpWebUrl());

        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new BadRequestException("Email already in use");
        }

        User user = User.builder()
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .firstName(registerDto.getFirstName().toUpperCase())
                .lastName(registerDto.getLastName().toUpperCase())
                .isActive(false)
                .mfaEnabled(false)
                .locale("es")
                .timezone("America/Lima")
                .defaultCurrency("PEN")
                .build();

        user = userRepository.save(user);

        // Generate unique confirmation token
        String confirmationToken = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusNanos(TimeUtils.parseDuration(this.mailBean.getEmailConfirmationExpiration()) * 1_000_000);
        EmailConfirmationToken emailToken = EmailConfirmationToken.builder()
                .token(confirmationToken)
                .userId(user.getId())
                .email(user.getEmail())
                .createdAt(now)
                .expiresAt(expiresAt)
                .isUsed(false)
                .build();

        emailConfirmationTokenRepository.save(emailToken);

        String confirmBaseUrl = this.mailBean.getSmtpWebUrl() + "/auth/confirm-email";
        Map<String, Object> vars = new HashMap<>();
        vars.put("email", user.getEmail());
        vars.put("firstName", user.getFirstName() != null ? user.getFirstName() : "Usuario");
        vars.put("confirmUrl", confirmBaseUrl + "?token=" + confirmationToken);

        mailService.sendEmail(user.getEmail(), user.getFirstName(), EmailTemplateType.CONFIRM_EMAIL, vars);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful. Please check your email for confirmation link.");
        return response;
    }

    /**
     * Login user with email and password
     */
    public TokenPairDto login(LoginDto loginDto, HttpServletRequest request) {
        log.info("Login attempt for email: {}", loginDto.getEmail());

        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.getIsActive()) {
            log.warn("Login attempt for inactive user: {}", loginDto.getEmail());
            throw new ForbiddenException("User account is not active. Please verify your email.");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", loginDto.getEmail());
            throw new UnauthorizedException("Invalid credentials");
        }

        if (user.getMfaEnabled() != null && user.getMfaEnabled()) {
            log.info("MFA required for user: {}", loginDto.getEmail());
            return TokenPairDto.builder()
                    .requireMfa(true)
                    .userId(user.getId().toString())
                    .message("Two-factor authentication required")
                    .build();
        }

        DeviceInfo deviceInfo = deviceInfoService.getDeviceInfo(request);
        return issueTokensForUser(user, deviceInfo);
    }

    /**
     * Verify MFA code and issue tokens
     */
    public TokenPairDto verifyMfa(MfaVerifyDto verifyDto, HttpServletRequest request) {
        log.info("MFA verification for user: {}", verifyDto.getUserId());

        User user = userRepository.findById(verifyDto.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Invalid request"));

        if (user.getMfaSecret() == null) {
            throw new BadRequestException("MFA not configured for this user");
        }

        if (!mfaService.verifyCode(verifyDto.getCode(), user.getMfaSecret())) {
            log.warn("Invalid MFA code for user: {}", user.getId());
            throw new UnauthorizedException("Invalid MFA code");
        }

        DeviceInfo deviceInfo = deviceInfoService.getDeviceInfo(request);
        return issueTokensForUser(user, deviceInfo);
    }

    /**
     * Logout user by revoking session
     */
    public void logout(UUID userId, String refreshToken) {
        log.info("Logout for user: {}", userId);
        sessionService.revokeAllUserSessions(userId, "logout");
    }

    /**
     * Get all active sessions for user
     */
    @Cacheable(value = "userSessions", key = "#userId")
    public List<Session> getActiveSessions(UUID userId) {
        log.debug("Fetching active sessions for user: {}", userId);
        return sessionService.getActiveSessions(userId);
    }

    /**
     * Revoke a specific session
     */
    @CacheEvict(value = "userSessions", key = "#userId")
    public void revokeSession(UUID userId, UUID sessionId, String reason) {
        log.info("Revoking session {} for user: {}", sessionId, userId);
        sessionService.revokeSession(sessionId, reason);
    }

    /**
     * Refresh access token using refresh token
     */
    public TokenPairDto refreshTokens(String refreshToken) {
        UUID userId = UUID.fromString(jwtProvider.getUserIdFromToken(refreshToken));
        log.info("Refreshing tokens for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Access denied"));

        Session session = sessionService.getSessionByRefreshToken(refreshToken, userId)
                .orElseThrow(() -> new UnauthorizedException("Access denied"));

        if (!sessionService.isSessionValid(session)) {
            log.warn("Invalid session for user: {}", userId);
            throw new UnauthorizedException("Session expired or revoked");
        }

        String email = user.getEmail();

        String newAccessToken = jwtProvider.generateAccessToken(userId, email);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        session.setAccessToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setAccessExpiresAt(LocalDateTime.now().plusNanos(TimeUtils.parseDuration(jwtBean.getAccessExpiration())));
        session.setRefreshExpiresAt(LocalDateTime.now().plusNanos(TimeUtils.parseDuration(jwtBean.getRefreshExpiration())));
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);

        log.info("Tokens refreshed for user: {}", userId);

        return TokenPairDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    /**
     * Confirm email using token
     */
    @Transactional
    public Map<String, String> confirmEmail(String token) {
        log.info("Confirming email with token");

        try {
            EmailConfirmationToken confirmationToken = emailConfirmationTokenRepository.findValidToken(token)
                    .orElseThrow(() -> new BadRequestException("Invalid confirmation token"));

            // Check if token is expired
            if (LocalDateTime.now().isAfter(confirmationToken.getExpiresAt())) {
                throw new BadRequestException("Confirmation token has expired");
            }

            if (confirmationToken.getIsUsed()) {
                throw new BadRequestException("Confirmation token already used");
            }

            User user = userRepository.findById(confirmationToken.getUserId())
                    .orElseThrow(() -> new BadRequestException("User not found"));

            if (user.getIsActive()) {
                Map<String, String> res = new HashMap<>();
                res.put("message", "Email already confirmed");
                return res;
            }

            user.setIsActive(true);
            user.setEmailVerifiedAt(LocalDateTime.now());
            userRepository.save(user);
            userRepository.flush();

            // Mark token as used
            confirmationToken.setIsUsed(true);
            confirmationToken.setConfirmedAt(LocalDateTime.now());
            emailConfirmationTokenRepository.save(confirmationToken);

            String loginUrl = this.mailBean.getSmtpWebUrl() + "/auth/login";
            Map<String, Object> vars = new HashMap<>();
            vars.put("appUrl", loginUrl);
            vars.put("firstName", user.getFirstName() != null ? user.getFirstName() : "Usuario");

            mailService.sendEmail(user.getEmail(), user.getFirstName(), EmailTemplateType.WELCOME, vars);

            log.info("Email confirmed for user: {}", user.getId());

            Map<String, String> res = new HashMap<>();
            res.put("message", "Email confirmed successfully");
            return res;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error confirming email: {}", e.getMessage());
            throw new BadRequestException("Invalid or expired token");
        }
    }

    /**
     * Clear user cache after email confirmation
     */
    @CacheEvict(value = "userCache", allEntries = true)
    public void clearUserCache() {
        log.debug("Clearing user cache");
    }

    /**
     * Get cached user by ID
     */
    @Cacheable(value = "userCache", key = "#userId")
    public User getUserById(String userId) {
        log.debug("Fetching user by ID: {}", userId);
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Get cached user by email
     */
    @Cacheable(value = "userCache", key = "'email:' + #email")
    public User getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Issue tokens for user after successful authentication
     */
    private TokenPairDto issueTokensForUser(User user, DeviceInfo deviceInfo) {
        log.info("Issuing tokens for user: {}", user.getId());

        UUID userId = user.getId();
        String email = user.getEmail();

        String accessToken = jwtProvider.generateAccessToken(userId, email);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessExpiresAt = now.plusNanos(TimeUtils.parseDuration(jwtBean.getAccessExpiration()));
        LocalDateTime refreshExpiresAt = now.plusNanos(TimeUtils.parseDuration(jwtBean.getRefreshExpiration()));

        Session session = sessionService.createSession(userId, accessToken, refreshToken,
                accessExpiresAt, refreshExpiresAt, deviceInfo);

        // Create or update device for this user
        sessionService.createOrUpdateDevice(userId, deviceInfo);

        user.setLastLogin(now);
        userRepository.save(user);

        return TokenPairDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToDto(user))
                .build();
    }

    /**
     * Map User entity to DTO
     */
    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .phoneNumber(user.getPhoneNumber())
                .locale(user.getLocale())
                .timezone(user.getTimezone())
                .defaultCurrency(user.getDefaultCurrency())
                .isActive(user.getIsActive())
                .mfaEnabled(user.getMfaEnabled())
                .lastLogin(user.getLastLogin())
                .emailVerifiedAt(user.getEmailVerifiedAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
