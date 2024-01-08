package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbsenceDto {

    Integer id;
    LocalDate dateFrom;
    LocalDate dateTo;
    Integer volunteerId;

}
