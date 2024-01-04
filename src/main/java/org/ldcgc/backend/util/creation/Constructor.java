package org.ldcgc.backend.util.creation;

import org.ldcgc.backend.exception.ApiError;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.util.conversion.Convert;
import org.ldcgc.backend.util.retrieving.Messages;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Constructor {

    private static Response.DTO buildResponseMessage(String message) {
        return Response.DTO.builder().message(message).build();
    }

    private static Response.DTO buildResponseMessageDetails(String message, List<String> details) {
        return Response.DTO.builder().message(message).details(details).build();
    }

    private static Response.DTOWithLocation buildResponseMessageLocation(String message, String location) {
        return Response.DTOWithLocation.builder().message(message).location(location).build();
    }

    private static Response.DTO buildResponseMessageObject(String message, Object object) {
        return Response.DTO.builder().message(message).data(object).build();
    }

    private static Response.DTO buildResponseData(Object object) {
        return Response.DTO.builder().data(object).build();
    }

    public static ResponseEntity<?> generic501() {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.NOT_IMPLEMENTED)
                .status(HttpStatus.NOT_IMPLEMENTED.value())
                .endpoint(MDC.get("requestURI"))
                .timestamp(Convert.nowToTimeStampString())
                .message(Messages.Error.ENDPOINT_NOT_IMPLEMENTED)
                .build();
        return buildResponseMessageObject(HttpStatus.NOT_IMPLEMENTED, Messages.Error.ENDPOINT_NOT_IMPLEMENTED, apiError);
    }

    public static ResponseEntity<?> buildResponseMessage(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus).body(buildResponseMessage(message));
    }

    public static ResponseEntity<?> buildResponseMessageObject(HttpStatus httpStatus, String message, Object object) {
        return ResponseEntity.status(httpStatus).body(buildResponseMessageObject(message, object));
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

    public static ResponseEntity<?> buildResponseMessageObjectHeader(HttpStatus httpStatus, String message, Object object, HttpHeaders headers) {
        return ResponseEntity.status(httpStatus).headers(headers).body(buildResponseMessageObject(message, object));
    }

    // exceptions

    public static ResponseEntity<Object> buildExceptionResponseObject(HttpStatus httpStatus, Object object) {
        return ResponseEntity.status(httpStatus).body(buildResponseData(object));
    }

    public static ResponseEntity<?> buildResponseObjectLocation(HttpStatus httpStatus, String message, String location, HttpHeaders headers) {
        return ResponseEntity.status(httpStatus).headers(headers).body(buildResponseMessageLocation(message, location));
    }

    public static ResponseEntity<Object> buildExceptionResponseMessageObject(HttpStatus httpStatus, String message, Object object) {
        return ResponseEntity.status(httpStatus).body(buildResponseMessageObject(message, object));
    }
}
