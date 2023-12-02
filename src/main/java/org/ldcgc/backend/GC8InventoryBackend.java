package org.ldcgc.backend;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.configuration.ContextConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableTransactionManagement
@SpringBootApplication
@RequiredArgsConstructor
public class GC8InventoryBackend {

	private final ContextConstants contextConstants;

	private static GC8InventoryBackend INSTANCE;

	@PostConstruct
	public void init() {
		GC8InventoryBackend.INSTANCE = this;
	}

	public static void main(String[] args) {
		SpringApplication.run(GC8InventoryBackend.class, args);

		log.info("Api running! Check swagger here: http://localhost:{}/api/swagger-ui/index.html",
			INSTANCE.contextConstants.getTomcatPort());
	}

}
