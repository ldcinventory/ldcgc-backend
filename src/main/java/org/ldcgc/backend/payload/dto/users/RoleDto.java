package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDto {

    private Integer id;
    @Schema(requiredMode = REQUIRED)
    private String name;
    private String description;
    private List<RoleRegisterDto> rolesRegistered;

}
