package com.yalice.wardrobe_social_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Logger component that runs at application startup.
 * Logs important configuration information for debugging purposes.
 */
@Component
public class StartupLogger implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    /**
     * Method executed on application startup.
     * Logs the database connection URL for verification.
     *
     * @param args Command line arguments passed to the application
     * @throws Exception If an error occurs during execution
     */
    @Override
    public void run(String... args) throws Exception {
        logger.info("Datasource URL: {}", datasourceUrl);
    }
}
