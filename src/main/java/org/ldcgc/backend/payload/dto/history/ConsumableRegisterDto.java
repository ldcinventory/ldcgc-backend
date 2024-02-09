package org.ldcgc.backend.payload.dto.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;

import java.time.LocalDateTime;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumableRegisterDto {

    Integer id;
    LocalDateTime inRegistration;
    LocalDateTime outRegistration;
    Integer stockAmount;
    ConsumableDto tool;
    VolunteerDto volunteer;

}
