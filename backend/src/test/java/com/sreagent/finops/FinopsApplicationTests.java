package com.sreagent.finops;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.ai.vertex.ai.gemini.project-id=test-project",
    "spring.ai.vertex.ai.gemini.location=us-central1"
})
class FinopsApplicationTests {

	@Test
	void contextLoads() {
	}

}
