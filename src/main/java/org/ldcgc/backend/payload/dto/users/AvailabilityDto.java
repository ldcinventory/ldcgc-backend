package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.util.common.EWeekday;

import java.util.HashSet;
import java.util.Set;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailabilityDto {

    Integer volunteerId;
    Set<EWeekday> availabilityDays;

}
