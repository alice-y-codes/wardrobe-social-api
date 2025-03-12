package com.yalice.wardrobe_social_app.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ImageStorageConfig {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            String cleanPath = StringUtils.cleanPath(uploadDir);
            Path path = Paths.get(cleanPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }
}