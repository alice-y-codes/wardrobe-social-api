package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.configs.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleService {
    private final AppConfig appConfig;

    public void demonstrateConfig() {
        // Access database configuration
        log.info("Database URL: {}", appConfig.getDatabase().getUrl());

        // Access JWT configuration
        log.info("JWT Expiration: {} ms", appConfig.getJwt().getExpiration());

        // Access server configuration
        log.info("Server Port: {}", appConfig.getServer().getPort());

        // Access logging configuration
        log.info("Root logging level: {}", appConfig.getLogging().getRootLevel());
        log.info("Application logging level: {}", appConfig.getLogging().getAppLevel());
    }
}