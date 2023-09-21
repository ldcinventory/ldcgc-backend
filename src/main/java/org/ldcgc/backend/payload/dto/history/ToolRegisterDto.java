package org.ldcgc.backend.payload.dto.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolRegisterDto implements Serializable {

    Integer id;
    LocalDateTime inRegistration;
    LocalDateTime outRegistration;
    ToolDto tool;
    VolunteerDto volunteer;

}
