package com.example.DigitalStore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Tillåt alla endpoints
                .allowedOrigins("http://localhost:8080", "http://localhost:8081") // Lägg till dina Swagger-ursprung
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Tillåt specifika HTTP-metoder
                .allowedHeaders("*") // Tillåt alla headers
                .allowCredentials(true); // Tillåt autentisering (om nödvändigt)
    }
}
