package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

public class LocationLvl1 implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        Integer id;
        String name;
        String description;
        Location.DTO location;
        List<LocationLvl2.DTO> lvl2;
    }

}
