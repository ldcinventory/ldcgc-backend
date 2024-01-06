package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.util.common.EWeekday;

import java.io.Serializable;
import java.util.List;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VolunteerDto implements Serializable {

    Integer id;
    String name;
    String lastName;
    // also, this id will be their barcode
    String builderAssistantId;
    Boolean isActive;
    List<EWeekday> availability;
    List<AbsenceDto> absences;
    GroupDto groupDto;

}
