package org.ldcgc.backend.exception;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;

import java.util.List;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ApiException extends Exception {
    private final int httpStatusCode;
    private final String localizedMessage;
    @Singular("error")
    private final List<ApiSubError> errors;

}
