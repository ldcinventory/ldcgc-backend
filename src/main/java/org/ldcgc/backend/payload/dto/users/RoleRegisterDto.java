package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.util.common.ERoleStatus;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleRegisterDto {

    private Integer id;
    @Schema(requiredMode = REQUIRED)
    private Integer linkedId;
    @Schema(requiredMode = REQUIRED)
    private String linkedTableName;
    private Integer location;
    @Schema(requiredMode = REQUIRED)
    private LocalDate validFrom;
    private LocalDate validUntil;
    @Schema(requiredMode = REQUIRED)
    private ERoleStatus status;
    @Schema(requiredMode = REQUIRED)
    private String assignedId;
    @Schema(requiredMode = REQUIRED)
    private String assignedTableName;
    @Schema(requiredMode = REQUIRED)
    private RoleDto roleDto;

}
