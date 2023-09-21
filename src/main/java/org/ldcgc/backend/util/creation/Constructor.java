package org.ldcgc.backend.util.creation;

import org.ldcgc.backend.exception.ApiError;
import org.ldcgc.backend.exception.ApiSubError;
import org.ldcgc.backend.payload.dto.other.Message;
import org.ldcgc.backend.util.conversion.Convert;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.ldcgc.backend.util.retrieving.Messages.getErrorMessage;

@Component
public class Constructor {

    public static ApiSubError buildApiSubErrorMessage(String message) {
        return ApiSubError
                .builder()
                .message(message)
                .build();
    }

    public static ResponseEntity<?> generic501() {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.NOT_IMPLEMENTED)
                .status(HttpStatus.NOT_IMPLEMENTED.value())
                .endpoint(MDC.get("requestURI"))
                .timestamp(Convert.nowToTimeStampString())
                .message(getErrorMessage("ENDPOINT_NOT_IMPLEMENTED"))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(apiError);
    }

    public static ResponseEntity<?> buildResponseMessage(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus).body(Message.DTO.builder().message(message).build());
    }

    public static ResponseEntity<?> buildResponseObject(HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).build();
    }

    public static ResponseEntity<?> buildResponseObject(HttpStatus httpStatus, Object object) {
        return ResponseEntity.status(httpStatus).body(object);
    }

    public static ResponseEntity<?> buildResponseObjectHeader(HttpStatus httpStatus, Object object, HttpHeaders headers) {
        return ResponseEntity.status(httpStatus).headers(headers).body(object);
    }

    public static ResponseEntity<?> buildResponseObjectMessage(HttpStatus httpStatus, String message, Object object) {
        return ResponseEntity.status(httpStatus).body(Message.DTOWithObject.builder().message(message).result(object).build());
    }

    public static ResponseEntity<?> buildResponseObjectLocation(HttpStatus httpStatus, String message, String location, HttpHeaders headers) {
        return ResponseEntity.status(httpStatus).headers(headers).body(Message.DTOWithLocation.builder().message(message).location(location).build());
    }
}
