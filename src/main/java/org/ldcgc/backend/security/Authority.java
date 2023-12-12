package org.ldcgc.backend.security;

public class Authority {
    public static class Role {

        public static final String USER_LEVEL = "hasAnyRole('USER', 'MANAGER', 'ADMIN')";
        public static final String MANAGER_LEVEL = "hasAnyRole('MANAGER', 'ADMIN')";
        public static final String ADMIN_LEVEL = "hasAnyRole('ADMIN')";

    }
}
