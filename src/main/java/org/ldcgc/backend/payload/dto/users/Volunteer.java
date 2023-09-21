package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

public class Volunteer implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        Integer id;
        String name;
        String lastName;
        // also, this id will be their barcode
        String builderAssistantId;
        boolean isActive;
        Availability.DTO availability;
        List<Absence.DTO> absences;
    }

}
