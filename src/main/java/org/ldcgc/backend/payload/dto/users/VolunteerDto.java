package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.util.common.EWeekday;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VolunteerDto {

    private Integer id;
    private String name;
    private String lastName;
    // this id will be their barcode
    private String builderAssistantId;
    private Boolean isActive;
    private List<EWeekday> availability;
    private List<AbsenceDto> absences;
    private GroupDto groupDto;

}
