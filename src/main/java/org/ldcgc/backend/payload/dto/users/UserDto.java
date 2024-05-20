package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.payload.dto.category.ResponsibilityDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.util.common.ERole;

import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Integer id;
    @Schema(requiredMode = REQUIRED)
    private String email;
    @Schema(requiredMode = REQUIRED)
    private String password;
    private ERole role;
    private VolunteerDto volunteer;
    private ResponsibilityDto responsibility;
    private GroupDto group;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime tokenExpires;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime refreshExpires;

}
