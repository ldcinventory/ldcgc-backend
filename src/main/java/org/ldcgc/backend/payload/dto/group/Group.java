package org.ldcgc.backend.payload.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.location.Location;

import java.io.Serializable;

public class Group implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        Integer id;
        String name;
        String description;
        String urlImage;
        String phoneNumber;
        Location location;
    }

}
