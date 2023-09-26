package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.category.SubCategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.util.common.ERole;

import java.io.Serializable;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {

    Integer id;
    String email;
    String password;
    ERole role;
    VolunteerDto volunteer;
    SubCategoryDto responsibility;
    GroupDto group;

}
