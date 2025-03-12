package com.yalice.wardrobe_social_app.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    private final Database database = new Database();
    private final Jwt jwt = new Jwt();
    private final Server server = new Server();
    private final Logging logging = new Logging();

    @Data
    public static class Database {
        private String url;
        private String username;
        private String password;
    }

    @Data
    public static class Jwt {
        private String secret;
        private long expiration;
    }

    @Data
    public static class Server {
        private int port;
    }

    @Data
    public static class Logging {
        private String rootLevel;
        private String appLevel;
    }
}