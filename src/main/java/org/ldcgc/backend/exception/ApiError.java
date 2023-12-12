package org.ldcgc.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HttpStatusCode httpStatus;

    private String endpoint;

    @JsonProperty("class")
    private String clazz;

    private String method;

    private String message;

    @Singular("error")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ApiSubError> errors;

}
