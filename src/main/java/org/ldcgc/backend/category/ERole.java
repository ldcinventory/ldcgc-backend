package org.ldcgc.backend.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ERole implements EnumMethods {

    @JsonProperty("standard")
    ROLE_STANDARD("standard"),
    @JsonProperty("manager")
    ROLE_MANAGER("manager"),
    @JsonProperty("admin")
    ROLE_ADMIN("admin");

    private final String roleName;

    public static ERole getEnumFromRoleName(String roleName) {
        for(ERole role : ERole.values()) {
            if(role.getRoleName().equals(roleName))
                return role;
        }

        return null;
    }

}
