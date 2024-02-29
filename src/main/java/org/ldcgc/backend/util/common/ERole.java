package org.ldcgc.backend.util.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ERole implements EnumMethods {

    @JsonProperty("user")
    ROLE_USER("USER"),
    @JsonProperty("manager")
    ROLE_MANAGER("MANAGER"),
    @JsonProperty("admin")
    ROLE_ADMIN("ADMIN");

    private final String roleName;

    public static ERole getEnumFromRoleName(String roleName) {
        for(ERole role : ERole.values()) {
            if(role.getRoleName().equals(roleName))
                return role;
        }

        return null;
    }

}
