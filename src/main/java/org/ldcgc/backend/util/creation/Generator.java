package org.ldcgc.backend.util.creation;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Generator {

    public static String getEncryptedPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

}
