package com.codesoftlabs.umbral.security;

import com.codesoftlabs.umbral.util.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractTokenFromRequest(request);
            log.debug("JWT token extracted: {}", jwt != null ? "present" : "null");

            if (StringUtils.hasText(jwt)) {
                log.debug("Validating JWT token...: {}", jwt);
                if (jwtProvider.validateToken(jwt)) {
                    String userId = jwtProvider.getUserIdFromToken(jwt);
                    log.debug("JWT validation successful for user: {}", userId);
                    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(userId);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    request.setAttribute("userId", userId);
                    request.setAttribute("userEmail", userDetails.user().getEmail());
                    log.info("Set user authentication for user: {}", userId);
                } else {
                    log.warn("JWT validation failed for token");
                }
            } else {
                log.debug("No JWT token found in request");
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AppConstants.JWT_HEADER);
        log.debug("Authorization header value: {}", bearerToken != null ? "present" : "null");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AppConstants.JWT_PREFIX)) {
            return bearerToken.substring(AppConstants.JWT_PREFIX.length());
        }
        return null;
    }
}
