package com.yalice.wardrobe_social_app;

import com.yalice.wardrobe_social_app.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class WardrobeSocialAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
