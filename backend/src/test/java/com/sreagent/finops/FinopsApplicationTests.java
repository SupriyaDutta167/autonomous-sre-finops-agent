package com.sreagent.finops;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.ai.google.genai.api-key=test-api-key"
})
class FinopsApplicationTests {

	@Test
	void contextLoads() {
	}

}
