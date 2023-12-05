package org.ldcgc.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RequestException extends RuntimeException {

    private HttpStatus httpStatus;
    private ApiError apiError;
    private String location;
    //TODO: Incluir objetos a través del constructor. Cuando esté hecho, refactorizar CategoryServiceImpl (linea 33), LocationServiceImpl (linea 33) y GroupServiceImpl (línea 33)

    public RequestException(String message) {
        super(message);
    }

    public RequestException(HttpStatus _httpStatus, String message) {
        super(message);
        httpStatus = _httpStatus;
    }

    public RequestException(HttpStatus _httpStatus, String message, ApiError _apiError) {
        super(message);
        httpStatus = _httpStatus;
        apiError = _apiError;
    }

    public RequestException(HttpStatus _httpStatus, String message, String _location) {
        super(message);
        httpStatus = _httpStatus;
        location = _location;
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestException(ApiError _apiError, String message) {
        super(message);
        apiError = _apiError;
        httpStatus = HttpStatus.valueOf(_apiError.getHttpStatus().value());
    }

}
