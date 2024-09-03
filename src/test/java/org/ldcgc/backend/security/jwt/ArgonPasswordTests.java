package org.ldcgc.backend.security.jwt;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.app.security.jwt.ArgonPassword;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ArgonPasswordTests {

    @InjectMocks private ArgonPassword argonPassword;

    @Test
    public void passwordEncoderValidArgsTest() throws IllegalAccessException {
        FieldUtils.writeField(argonPassword, "argonSaltLength", 16, true);
        FieldUtils.writeField(argonPassword, "argonHashLength", 32, true);
        FieldUtils.writeField(argonPassword, "argonThreads", 4, true);
        FieldUtils.writeField(argonPassword, "argonMemory", 65536, true);
        FieldUtils.writeField(argonPassword, "argonIterations", 3, true);

        Argon2PasswordEncoder encoder = argonPassword.passwordEncoder();
        assertNotNull(encoder);

    }

    @Test
    void testPasswordEncoderWithNullArguments() throws IllegalAccessException {
        FieldUtils.writeField(argonPassword, "argonSaltLength", null, true);
        FieldUtils.writeField(argonPassword, "argonHashLength", null, true);
        FieldUtils.writeField(argonPassword, "argonThreads", null, true);
        FieldUtils.writeField(argonPassword, "argonMemory", null, true);
        FieldUtils.writeField(argonPassword, "argonIterations", null, true);

        Argon2PasswordEncoder encoder = argonPassword.passwordEncoder();
        assertNotNull(encoder);
    }

}
