package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.category.EStatus;

import java.io.Serializable;

public class Status implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        EStatus name;
    }

}
