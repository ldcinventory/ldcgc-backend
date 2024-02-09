package org.ldcgc.backend.payload.dto.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class Response {

    @Getter @Builder(toBuilder = true) @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        private String message;
        private List<String> details;
        private Object data;
    }

    @Getter @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTOWithLocation {
        private String message;
        private String location;
    }

}
