package org.ldcgc.backend.util.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.util.compare.EnumMethods;

@RequiredArgsConstructor
@Getter
public enum ERole implements EnumMethods {

    @JsonProperty("user")
    ROLE_USER("user"),
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
