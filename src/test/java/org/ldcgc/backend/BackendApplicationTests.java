package org.ldcgc.backend;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
@ExtendWith(GlobalTestConfig.class)
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("Single test successful")
	void testSingleSuccessTest() {
		log.info("Success");
	}

	@Test
	@Disabled("Not implemented yet")
	void testShowSomething() {
	}


}
