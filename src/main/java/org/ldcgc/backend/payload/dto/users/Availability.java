package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.category.EWeekday;

import java.io.Serializable;
import java.util.List;

public class Availability implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        List<EWeekday> availabilityDays;
        Volunteer.DTO volunteer;
    }

}
