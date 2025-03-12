package com.yalice.wardrobe_social_app;

import com.yalice.wardrobe_social_app.configs.TestConfig;
import com.yalice.wardrobe_social_app.configs.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({ TestConfig.class, TestSecurityConfig.class })
class WardrobeSocialAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
