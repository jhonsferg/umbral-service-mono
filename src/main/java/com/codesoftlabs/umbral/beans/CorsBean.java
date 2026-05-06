package com.codesoftlabs.umbral.beans;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CorsBean {
    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private Boolean allowCredentials;
    private Long maxAge;
}
