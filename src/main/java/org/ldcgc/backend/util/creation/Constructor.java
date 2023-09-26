package org.ldcgc.backend.util.creation;

import org.ldcgc.backend.exception.ApiError;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.util.conversion.Convert;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.ENDPOINT_NOT_IMPLEMENTED;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Component
public class Constructor {

    public static ResponseEntity<?> generic501() {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.NOT_IMPLEMENTED)
                .status(HttpStatus.NOT_IMPLEMENTED.value())
                .endpoint(MDC.get("requestURI"))
                .timestamp(Convert.nowToTimeStampString())
                .message(getErrorMessage(ENDPOINT_NOT_IMPLEMENTED))
                .build();
        return buildResponseMessageObject(HttpStatus.NOT_IMPLEMENTED, getErrorMessage(ENDPOINT_NOT_IMPLEMENTED), apiError);
    }

    public static ResponseEntity<?> buildResponseMessage(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus).body(buildResponseMessage(message));
    }

    public static ResponseEntity<?> buildResponseDetailedMessage(HttpStatus httpStatus, String message, List<String> details) {
        return ResponseEntity.status(httpStatus).body(buildResponseMessageDetails(message, details));
    }

    public static ResponseEntity<?> buildResponseObject(HttpStatus httpStatus, Object object) {
        return ResponseEntity.status(httpStatus).body(buildResponseData(object));
    }

    public static ResponseEntity<?> buildResponseObjectHeader(HttpStatus httpStatus, Object object, HttpHeaders headers) {
        return ResponseEntity.status(httpStatus).headers(headers).body(buildResponseData(object));
    }

    public static ResponseEntity<?> buildResponseMessageObject(HttpStatus httpStatus, String message, Object object) {
        return ResponseEntity.status(httpStatus).body(buildResponseMessageObject(message, object));
    }

    public static ResponseEntity<?> buildResponseObjectLocation(HttpStatus httpStatus, String message, String location, HttpHeaders headers) {
        return ResponseEntity.status(httpStatus).headers(headers).body(buildResponseMessageLocation(message, location));
    }

    private static Response.DTO buildResponseMessage(String message) {
        return Response.DTO.builder().status(message).build();
    }

    private static Response.DTO buildResponseMessageDetails(String message, List<String> details) {
        return Response.DTO.builder().status(message).details(details).build();
    }

    private static Response.DTOWithLocation buildResponseMessageLocation(String message, String location) {
        return Response.DTOWithLocation.builder().status(message).location(location).build();
    }

    private static Response.DTO buildResponseData(Object object) {
        return Response.DTO.builder().data(object).build();
    }

    private static Response.DTO buildResponseMessageObject(String message, Object object) {
        return Response.DTO.builder().status(message).data(object).build();
    }
}
