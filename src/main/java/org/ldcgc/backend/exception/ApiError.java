package org.ldcgc.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.http.HttpStatusCode;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private String timestamp;

    private int status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HttpStatusCode httpStatus;

    private String endpoint;

    private String message;

    @Singular("error")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ApiSubError> errors;

}
