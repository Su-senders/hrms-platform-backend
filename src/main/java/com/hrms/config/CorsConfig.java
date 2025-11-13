package com.hrms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration for the application.
 */
@Configuration
public class CorsConfig {

    @Value("${hrms.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${hrms.cors.allowed-methods}")
    private String[] allowedMethods;

    @Value("${hrms.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${hrms.cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${hrms.cors.max-age}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        configuration.setAllowedHeaders(List.of(allowedHeaders));
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

        // Allow tenant header
        configuration.addExposedHeader("X-Tenant-ID");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
