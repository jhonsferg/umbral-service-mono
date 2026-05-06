package com.codesoftlabs.umbral.config;

import com.codesoftlabs.umbral.beans.CorsBean;
import com.codesoftlabs.umbral.beans.JwtBean;
import com.codesoftlabs.umbral.beans.MailBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${umbral.jwt.issuer}")
    private String tokenIssuer;
    @Value("${umbral.jwt.access.secret}")
    private String accessSecret;
    @Value("${umbral.jwt.access.expiration}")
    private String accessExpiration;
    @Value("${umbral.jwt.refresh.secret}")
    private String refreshSecret;
    @Value("${umbral.jwt.refresh.expiration}")
    private String refreshExpiration;

    @Value("${umbral.smtp.web.url}")
    private String smtpWebUrl;
    @Value("${umbral.email.confirmation.expiration}")
    private String emailConfirmationExpiration;

    @Value("${umbral.app.cors.allowed-origins}")
    private String allowedOrigins;
    @Value("${umbral.app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethods;
    @Value("${umbral.app.cors.allowed-headers:Content-Type,Authorization,X-Requested-With}")
    private String allowedHeaders;
    @Value("${umbral.app.cors.allow-credentials:true}")
    private boolean allowCredentials;
    @Value("${umbral.app.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public JwtBean generateJwtBean() {
        return JwtBean.builder()
                .tokenIssuer(this.tokenIssuer)
                .accessSecret(this.accessSecret)
                .accessExpiration(this.accessExpiration)
                .refreshSecret(this.refreshSecret)
                .refreshExpiration(this.refreshExpiration)
                .build();
    }

    @Bean
    public MailBean generateMailBean() {
        return MailBean.builder()
                .smtpWebUrl(this.smtpWebUrl)
                .emailConfirmationExpiration(this.emailConfirmationExpiration)
                .build();
    }

    @Bean
    public CorsBean generateCorsBean() {
        return CorsBean.builder()
                .allowedOrigins(this.allowedOrigins)
                .allowedMethods(this.allowedMethods)
                .allowedHeaders(this.allowedHeaders)
                .allowCredentials(this.allowCredentials)
                .maxAge(this.maxAge)
                .build();
    }
}
