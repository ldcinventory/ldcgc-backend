package org.ldcgc.backend.payload.dto.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        String message;
        List<String> details;
    }

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTOWithObject {
        String message;
        List<String> details;
        Object result;
    }

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTOWithLocation {
        String message;
        String location;
    }

}
