package org.ldcgc.backend.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ERole {

    ROLE_STANDARD("standard"),
    ROLE_MANAGER("manager"),
    ROLE_ADMIN("admin");

    private final String roleName;

    public static ERole getEnumFromRoleName(String roleName) {
        for(ERole role : ERole.values()) {
            if(role.getRoleName().equals(roleName))
                return role;
        }

        return null;
    }

    public final boolean equalsAny(ERole ...roles) {
        for (ERole role : roles)
            if (this == role) return true;
        return false;
    }

}
