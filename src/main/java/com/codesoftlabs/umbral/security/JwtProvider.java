package com.codesoftlabs.umbral.security;

import com.codesoftlabs.umbral.beans.JwtBean;
import com.codesoftlabs.umbral.util.TimeUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class JwtProvider {
    private JwtBean jwtBean;

    private Key getSigningKey() {
        byte[] keyBytes = jwtBean.getAccessSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UUID userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId);
        claims.put("email", email);
        return createToken(claims, userId.toString(), TimeUtils.parseDuration(jwtBean.getAccessExpiration()));
    }

    public String generateRefreshToken(UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId);
        claims.put("type", "REFRESH");
        return createToken(claims, userId.toString(), TimeUtils.parseDuration(jwtBean.getRefreshExpiration()));
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        log.info(String.valueOf(claims));
        return (String) claims.get("sub");
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (String) claims.get("email");
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getExpirationTime(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().getTime();
    }

    public long getTimeRemainingInSeconds(String token) {
        long expirationTime = getExpirationTime(token);
        long currentTime = System.currentTimeMillis();
        long remainingTime = expirationTime - currentTime;
        return Math.max(0, remainingTime / 1000);
    }

    private Key getAccessKey() {
        return Keys.hmacShaKeyFor(jwtBean.getAccessSecret().getBytes());
    }

    public String getUserIdFromAccessToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getAccessKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateAccessToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getAccessKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
