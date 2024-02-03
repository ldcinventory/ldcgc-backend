package org.ldcgc.backend.payload.dto.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.util.List;

public class Response {

    @Value @Builder(toBuilder = true) @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        String message;
        List<String> details;
        Object data;
    }

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTOWithLocation {
        String message;
        String location;
    }

}
