package com.codesoftlabs.umbral.service;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DeviceInfoService {

    /**
     * Extract device information from HTTP request
     */
    public Map<String, String> extractDeviceInfo(HttpServletRequest request) {
        Map<String, String> deviceInfo = new HashMap<>();

        String userAgent = request.getHeader("User-Agent");
        String ipAddress = extractClientIp(request);

        deviceInfo.put("userAgent", userAgent != null ? userAgent : "Unknown");
        deviceInfo.put("ipAddress", ipAddress);

        if (userAgent != null) {
            UserAgent ua = new UserAgent(userAgent);
            OperatingSystem os = ua.getOperatingSystem();
            Browser browser = ua.getBrowser();

            deviceInfo.put("platform", os.getDeviceType().getName());
            deviceInfo.put("osName", os.getName());
            deviceInfo.put("browserName", browser.getName());

            String deviceName = String.format("%s - %s", os.getName(), browser.getName());
            deviceInfo.put("deviceName", deviceName);
        } else {
            deviceInfo.put("platform", "Unknown");
            deviceInfo.put("osName", "Unknown");
            deviceInfo.put("browserName", "Unknown");
            deviceInfo.put("deviceName", "Unknown Device");
        }

        return deviceInfo;
    }

    /**
     * Extract client IP address from request
     */
    public String extractClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_FORWARDED_PROTO",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String value = request.getHeader(header);
            if (value != null && !value.isEmpty() && !"unknown".equalsIgnoreCase(value)) {
                String[] ips = value.split(",");
                String ip = ips[0].trim();
                if (isValidIp(ip)) {
                    return ip;
                }
            }
        }

        return request.getRemoteAddr();
    }

    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !ip.equals("0.0.0.0");
    }

    /**
     * Get device information object
     */
    public DeviceInfo getDeviceInfo(HttpServletRequest request) {
        Map<String, String> info = extractDeviceInfo(request);
        return DeviceInfo.builder()
                .userAgent(info.get("userAgent"))
                .ipAddress(info.get("ipAddress"))
                .platform(info.get("platform"))
                .deviceName(info.get("deviceName"))
                .osName(info.get("osName"))
                .browserName(info.get("browserName"))
                .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class DeviceInfo {
        private String userAgent;
        private String ipAddress;
        private String platform;
        private String deviceName;
        private String osName;
        private String browserName;
    }
}
