package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.entity.Device;
import com.codesoftlabs.umbral.entity.Session;
import com.codesoftlabs.umbral.repository.DeviceRepository;
import com.codesoftlabs.umbral.repository.SessionRepository;
import com.codesoftlabs.umbral.repository.UserRepository;
import com.codesoftlabs.umbral.service.DeviceInfoService.DeviceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    /**
     * Create a new session for user
     */
    public Session createSession(UUID userId, String accessToken, String refreshToken,
                                 LocalDateTime accessExpiresAt, LocalDateTime refreshExpiresAt,
                                 DeviceInfo deviceInfo) {
        log.debug("Creating session for user: {}", userId);

        Session session = Session.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessExpiresAt(accessExpiresAt)
                .refreshExpiresAt(refreshExpiresAt)
                .userAgent(deviceInfo.getUserAgent())
                .ipAddress(deviceInfo.getIpAddress())
                .platform(deviceInfo.getPlatform())
                .deviceName(deviceInfo.getDeviceName())
                .lastUsedAt(LocalDateTime.now())
                .isRevoked(false)
                .build();

        return sessionRepository.save(session);
    }

    /**
     * Get all active sessions for user
     */
    public List<Session> getActiveSessions(UUID userId) {
        log.debug("Fetching active sessions for user: {}", userId);
        return sessionRepository.findByUserIdAndIsRevokedFalseOrderByLastUsedAtDesc(userId);
    }

    /**
     * Get session by access token
     */
    public Optional<Session> getSessionByAccessToken(String accessToken) {
        return sessionRepository.findByAccessToken(accessToken);
    }

    /**
     * Get session by refresh token and user id
     */
    public Optional<Session> getSessionByRefreshToken(String refreshToken, UUID userId) {
        return sessionRepository.findByRefreshTokenAndUserId(refreshToken, userId);
    }

    /**
     * Update session last used time
     */
    public void updateSessionLastUsed(UUID sessionId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setLastUsedAt(LocalDateTime.now());
            sessionRepository.save(session);
        });
    }

    /**
     * Revoke a session
     */
    public void revokeSession(UUID sessionId, String reason) {
        log.info("Revoking session: {} with reason: {}", sessionId, reason);
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setIsRevoked(true);
            session.setRevokedAt(LocalDateTime.now());
            session.setRevokedReason(reason);
            sessionRepository.save(session);
        });
    }

    /**
     * Revoke all sessions for user
     */
    public void revokeAllUserSessions(UUID userId, String reason) {
        log.info("Revoking all sessions for user: {} with reason: {}", userId, reason);
        List<Session> sessions = sessionRepository.findByUserIdAndIsRevokedFalse(userId);
        LocalDateTime now = LocalDateTime.now();

        for (Session session : sessions) {
            session.setIsRevoked(true);
            session.setRevokedAt(now);
            session.setRevokedReason(reason);
        }

        sessionRepository.saveAll(sessions);
    }

    /**
     * Validate session - check if active and not expired
     */
    public boolean isSessionValid(Session session) {
        log.info("Checking if sessión is null");
        if (session == null) {
            return false;
        }

        log.info("Checking if sessión is revoked");
        if (session.getIsRevoked()) {
            return false;
        }

        log.info("Getting current local date time");
        LocalDateTime now = LocalDateTime.now();

        log.info("Comparing datetime: {}", !session.getAccessExpiresAt().isBefore(now));
        return !session.getAccessExpiresAt().isBefore(now);
    }

    /**
     * Delete expired sessions
     */
    @Transactional
    public void cleanupExpiredSessions() {
        log.debug("Cleaning up expired sessions");
        LocalDateTime now = LocalDateTime.now();

        List<Session> sessions = sessionRepository.findAll();
        sessions.removeIf(session -> session.getRefreshExpiresAt().isBefore(now) && session.getIsRevoked());

        sessionRepository.deleteAll(sessions);
    }

    /**
     * Get session by ID
     */
    public Optional<Session> getSessionById(UUID sessionId) {
        return sessionRepository.findById(sessionId);
    }

    /**
     * Check if user has an active session on specific device
     */
    public boolean hasActiveSessionOnDevice(UUID userId, String deviceName) {
        List<Session> sessions = getActiveSessions(userId);
        return sessions.stream()
                .anyMatch(s -> deviceName.equals(s.getDeviceName()) && !s.getIsRevoked());
    }

    /**
     * Update session device info
     */
    public void updateSessionDeviceInfo(UUID sessionId, DeviceInfo deviceInfo) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setUserAgent(deviceInfo.getUserAgent());
            session.setIpAddress(deviceInfo.getIpAddress());
            session.setPlatform(deviceInfo.getPlatform());
            session.setDeviceName(deviceInfo.getDeviceName());
            sessionRepository.save(session);
        });
    }

    /**
     * Create or update device for user
     */
    public Device createOrUpdateDevice(UUID userId, DeviceInfo deviceInfo) {
        log.debug("Creating or updating device for user: {} with userAgent: {}", userId, deviceInfo.getUserAgent());

        Optional<Device> existingDevice = deviceRepository.findByUserIdAndUserAgent(userId, deviceInfo.getUserAgent());

        if (existingDevice.isPresent()) {
            Device device = existingDevice.get();
            device.setLastActivityAt(LocalDateTime.now());
            return deviceRepository.save(device);
        }

        Device device = Device.builder()
                .userId(userId)
                .deviceName(deviceInfo.getDeviceName())
                .deviceType(deviceInfo.getPlatform())
                .operatingSystem(deviceInfo.getOsName())
                .browser(deviceInfo.getBrowserName())
                .userAgent(deviceInfo.getUserAgent())
                .ipAddress(deviceInfo.getIpAddress())
                .lastActivityAt(LocalDateTime.now())
                .isActive(true)
                .build();

        return deviceRepository.save(device);
    }

    /**
     * Get all devices for user
     */
    public List<Device> getUserDevices(UUID userId) {
        log.debug("Fetching all devices for user: {}", userId);
        return deviceRepository.findByUserIdOrderByLastActivityAtDesc(userId);
    }

    /**
     * Get active devices for user
     */
    public List<Device> getActiveUserDevices(UUID userId) {
        log.debug("Fetching active devices for user: {}", userId);
        return deviceRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Deactivate device
     */
    public void deactivateDevice(UUID deviceId) {
        log.info("Deactivating device: {}", deviceId);
        deviceRepository.findById(deviceId).ifPresent(device -> {
            device.setIsActive(false);
            deviceRepository.save(device);
        });
    }

    /**
     * Get device by ID
     */
    public Optional<Device> getDeviceById(UUID deviceId) {
        return deviceRepository.findById(deviceId);
    }
}
