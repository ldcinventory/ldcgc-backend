package org.ldcgc.backend.payload.dto.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.resources.Consumable;
import org.ldcgc.backend.payload.dto.users.Volunteer;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ConsumableRegister implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        Integer id;
        LocalDateTime inRegistration;
        LocalDateTime outRegistration;
        Integer stockLeft;
        Consumable.DTO tool;
        Volunteer.DTO volunteer;
    }

}
