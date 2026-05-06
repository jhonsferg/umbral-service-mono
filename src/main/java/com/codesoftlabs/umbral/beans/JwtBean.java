package com.codesoftlabs.umbral.beans;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtBean {
    private String accessSecret;
    private String accessExpiration;
    private String refreshSecret;
    private String refreshExpiration;
    private String tokenIssuer;
}
