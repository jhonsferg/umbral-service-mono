package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.beans.JwtBean;
import com.codesoftlabs.umbral.beans.MailBean;
import com.codesoftlabs.umbral.common.enums.EmailTemplateType;
import com.codesoftlabs.umbral.dto.LoginDto;
import com.codesoftlabs.umbral.dto.MfaVerifyDto;
import com.codesoftlabs.umbral.dto.RegisterDto;
import com.codesoftlabs.umbral.dto.TokenPairDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
        LocalDateTime expiresAt = now
                .plusNanos(TimeUtils.parseDuration(this.mailBean.getEmailConfirmationExpiration()) * 1_000_000);
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
        response.put("message", "Registro exitoso. Por favor, verifique su correo electrónico.");
        return response;
    }

    /**
     * Login user with email and password
     */
    public TokenPairDto login(LoginDto loginDto, HttpServletRequest request) {
        log.info("Login attempt for email: {}", loginDto.getEmail());

        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Usuario y/o contraseña incorrectos."));

        if (!user.getIsActive()) {
            log.warn("Login attempt for inactive user: {}", loginDto.getEmail());
            throw new ForbiddenException("Usuario no activo. Por favor, verifique su correo electrónico.");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", loginDto.getEmail());
            throw new UnauthorizedException("Usuario y/o contraseña incorrectos.");
        }

        if (user.getMfaEnabled() != null && user.getMfaEnabled()) {
            log.info("MFA required for user: {}", loginDto.getEmail());
            return TokenPairDto.builder()
                    .requireMfa(true)
                    .userId(user.getId().toString())
                    .message("Se requiere autenticación de dos factores.")
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
                .orElseThrow(() -> new UnauthorizedException("Solicitud inválida."));

        if (user.getMfaSecret() == null) {
            throw new BadRequestException("MFA no configurado para este usuario.");
        }

        if (!mfaService.verifyCode(verifyDto.getCode(), user.getMfaSecret())) {
            log.warn("Invalid MFA code for user: {}", user.getId());
            throw new UnauthorizedException("Código MFA inválido.");
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
    public List<Session> getActiveSessions(UUID userId) {
        log.debug("Fetching active sessions for user: {}", userId);
        return sessionService.getActiveSessions(userId);
    }

    /**
     * Revoke a specific session
     */
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
                .orElseThrow(() -> new UnauthorizedException("Acceso denegado."));

        Session session = sessionService.getSessionByRefreshToken(refreshToken, userId)
                .orElseThrow(() -> new UnauthorizedException("Acceso denegado."));

        if (!sessionService.isSessionValid(session)) {
            log.warn("Invalid session for user: {}", userId);
            throw new UnauthorizedException("Su sesión ha expirado o ha sido revocada.");
        }

        String email = user.getEmail();

        String newAccessToken = jwtProvider.generateAccessToken(userId, email);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        session.setAccessToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setAccessExpiresAt(
                LocalDateTime.now().plus(Duration.ofMillis(TimeUtils.parseDuration(jwtBean.getAccessExpiration()))));
        session.setRefreshExpiresAt(
                LocalDateTime.now().plus(Duration.ofMillis(TimeUtils.parseDuration(jwtBean.getRefreshExpiration()))));
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
                    .orElseThrow(() -> new BadRequestException("Token de confirmación inválido."));

            if (LocalDateTime.now().isAfter(confirmationToken.getExpiresAt())) {
                throw new BadRequestException("El token de confirmación ha expirado.");
            }

            if (confirmationToken.getIsUsed()) {
                throw new BadRequestException("El token de confirmación ya ha sido usado.");
            }

            User user = userRepository.findById(confirmationToken.getUserId())
                    .orElseThrow(() -> new BadRequestException("Usuario no encontrado."));

            if (user.getIsActive()) {
                Map<String, String> res = new HashMap<>();
                res.put("message", "Correo electrónico ya confirmado.");
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
            res.put("message", "Correo electrónico confirmado exitosamente.");
            return res;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error confirming email: {}", e.getMessage());
            throw new BadRequestException("Token de confirmación inválido o expirado.");
        }
    }

    /**
     * Clear user cache after email confirmation
     */
    public void clearUserCache() {
        log.debug("Clearing user cache");
    }

    /**
     * Get cached user by ID
     */
    public User getUserById(String userId) {
        log.debug("Fetching user by ID: {}", userId);
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
    }

    /**
     * Get cached user by email
     */
    public User getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
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
        LocalDateTime accessExpiresAt = now
                .plus(Duration.ofMillis(TimeUtils.parseDuration(jwtBean.getAccessExpiration())));
        LocalDateTime refreshExpiresAt = now
                .plus(Duration.ofMillis(TimeUtils.parseDuration(jwtBean.getRefreshExpiration())));

        Session session = sessionService.createSession(userId, accessToken, refreshToken, accessExpiresAt,
                refreshExpiresAt, deviceInfo);

        // Create or update device for this user
        sessionService.createOrUpdateDevice(userId, deviceInfo);

        user.setLastLogin(now);
        userRepository.save(user);

        return TokenPairDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
