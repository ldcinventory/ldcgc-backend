package org.ldcgc.backend.security.jwt;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.springframework.security.crypto.argon2.Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8;

@Component
public class ArgonPassword {

    @Value("${spring.security.crypto.password.argon2.salt-length}")
    private Integer argonSaltLength;
    @Value("${spring.security.crypto.password.argon2.hash-length}")
    private Integer argonHashLength;
    @Value("${spring.security.crypto.password.argon2.parallelism}")
    private Integer argonThreads;
    @Value("${spring.security.crypto.password.argon2.memory}")
    private Integer argonMemory;
    @Value("${spring.security.crypto.password.argon2.iterations}")
    private Integer argonIterations;

    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        if(ObjectUtils.anyNull(argonSaltLength, argonHashLength, argonThreads, argonMemory, argonIterations))
            return defaultsForSpringSecurity_v5_8();

        return new Argon2PasswordEncoder(argonSaltLength, argonHashLength, argonThreads, argonMemory, argonIterations);
    }
}
