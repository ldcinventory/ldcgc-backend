package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.category.EWeekday;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailabilityDto implements Serializable {

    Integer volunteerId;
    List<EWeekday> availabilityDays;

}
