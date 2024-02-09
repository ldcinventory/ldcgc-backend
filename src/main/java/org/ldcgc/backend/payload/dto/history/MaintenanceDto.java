package org.ldcgc.backend.payload.dto.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaintenanceDto {

    Integer id;
    LocalDateTime inRegistration;
    LocalDateTime outRegistration;
    ToolDto tool;
    VolunteerDto volunteer;
    CategoryDto inStatus;
    CategoryDto outStatus;

}
