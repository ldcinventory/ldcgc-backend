package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

public class LocationLvl2 implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        Integer id;
        String name;
        String description;
        LocationLvl1.DTO locationLvl1;
    }

}
