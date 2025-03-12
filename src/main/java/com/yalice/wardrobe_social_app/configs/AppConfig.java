package com.yalice.wardrobe_social_app.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    private final Jwt jwt = new Jwt();
    private final Upload upload = new Upload();

    @Data
    public static class Jwt {
        private String secret;
        private long expiration;
    }

    @Data
    public static class Upload {
        private String dir;
        private Image image = new Image();

        @Data
        public static class Image {
            private long maxSize;
            private String[] allowedTypes;
        }
    }
}