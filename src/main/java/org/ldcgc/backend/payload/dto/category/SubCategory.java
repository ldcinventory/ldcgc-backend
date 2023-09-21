package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

public class SubCategory implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        Integer id;
        String name;
        Boolean locked = false;
        Category.DTO category;
    }

}
