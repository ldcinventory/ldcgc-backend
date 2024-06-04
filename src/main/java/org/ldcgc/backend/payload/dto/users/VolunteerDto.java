package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.util.common.EVolunteerStatus;
import org.ldcgc.backend.util.common.EWeekday;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VolunteerDto {

    private Integer id;
    @Schema(requiredMode = REQUIRED)
    private String name;
    private String lastName;
    @Schema(requiredMode = REQUIRED)
    private String builderAssistantId;
    private EVolunteerStatus status;
    private List<EWeekday> availability;
    private List<AbsenceDto> absences;
    private GroupDto groupDto;

}
