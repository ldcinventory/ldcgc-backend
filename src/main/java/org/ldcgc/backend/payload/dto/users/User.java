package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.category.ERole;
import org.ldcgc.backend.payload.dto.category.SubCategory;
import org.ldcgc.backend.payload.dto.group.Group;

import java.io.Serializable;

public class User implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        Integer id;
        String email;
        String password;
        ERole role;
        Volunteer.DTO volunteer;
        SubCategory.DTO responsibility;
        Group.DTO group;
    }

}
