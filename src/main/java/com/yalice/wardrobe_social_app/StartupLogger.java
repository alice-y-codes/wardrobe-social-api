import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StartupLogger implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Datasource URL: {}", datasourceUrl);
    }
}