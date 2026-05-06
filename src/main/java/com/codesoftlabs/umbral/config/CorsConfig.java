package com.codesoftlabs.umbral.config;

import com.codesoftlabs.umbral.beans.CorsBean;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {
    private final CorsBean corsBean;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = this.corsBean.getAllowedOrigins().split(",");
        String[] methods = this.corsBean.getAllowedMethods().split(",");
        String[] headers = this.corsBean.getAllowedHeaders().split(",");

        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods(methods)
                .allowedHeaders(headers)
                .allowCredentials(this.corsBean.getAllowCredentials())
                .maxAge(this.corsBean.getMaxAge())
                .exposedHeaders("Authorization", "X-Total-Count", "X-Page-Number", "X-Page-Size");
    }
}
