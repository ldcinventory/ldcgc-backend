package org.ldcgc.backend;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.security.crypto.argon2.Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8;

@EnableTransactionManagement
@SpringBootApplication
public class BackendApplication {

	@Value("${ARGON_SALT_LENGTH}") private static Integer argonSaltLength;
	@Value("${ARGON_HASH_LENGTH}") private static Integer argonHashLength;
	@Value("${ARGON_THREADS}") 	   private static Integer argonThreads;
	@Value("${ARGON_MEMORY}")      private static Integer argonMemory;
	@Value("${ARGON_ITERATIONS}")  private static Integer argonIterations;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public Argon2PasswordEncoder passwordEncoder() {
		if(ObjectUtils.anyNull(argonSaltLength, argonHashLength, argonThreads, argonMemory, argonIterations))
			return defaultsForSpringSecurity_v5_8();

		return new Argon2PasswordEncoder(argonSaltLength, argonHashLength, argonThreads, argonMemory, argonIterations);
	}

}
