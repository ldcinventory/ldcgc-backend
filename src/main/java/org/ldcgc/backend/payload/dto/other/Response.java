package org.ldcgc.backend.payload.dto.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class Response {

    @Getter @SuperBuilder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        private String message;
        private List<String> details;
        private Object data;
    }

    @Getter @SuperBuilder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTOWithPaginationDetails extends DTO {
        private PaginationDetails paginationDetails;
    }

    @Getter @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTOWithLocation {
        private String message;
        private String location;
    }

}
