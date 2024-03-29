package org.ldcgc.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RequestException.class)
    public final ResponseEntity<?> handleRequestExceptions(RequestException ex) {

        if (ex.getApiError() != null)
            return new ResponseEntity<>(ex.getApiError(), new HttpHeaders(), ex.getApiError().getHttpStatus());

        if (StringUtils.isNotEmpty(ex.getLocation())) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", ex.getLocation());
            return Constructor.buildResponseObjectHeader(ex.getHttpStatus(), ex.getMessage(), headers);
        }

        ApiError apiError = ApiError.builder()
                .status(Optional.ofNullable(ex.getHttpStatus()).map(HttpStatus::value).orElse(HttpStatus.BAD_REQUEST.value()))
                .httpStatus(Optional.ofNullable(ex.getHttpStatus()).orElse(HttpStatus.BAD_REQUEST))
                .message(ex.getMessage())
                .build();

        log.error(String.format("%s :: %d :: RequestException: %s", ex, apiError.getHttpStatus().value(), Messages.Error.RUNTIME_EXCEPTION));

        return Constructor.buildResponseObjectHeader(
            HttpStatus.valueOf(apiError.getHttpStatus().value()), apiError, new HttpHeaders());
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, @NotNull HttpHeaders headers, HttpStatusCode status, @NotNull WebRequest request) {
        ApiError apiError = ApiError.builder()
            .status(status.value())
            .endpoint(Objects.requireNonNull(ex.getAllValidationResults().getFirst().getMethodParameter().getMethod()).getName())
            .clazz(ex.getAllValidationResults().getFirst().getMethodParameter().getDeclaringClass().getName())
            .method(((ServletWebRequest) request).getRequest().getAttribute("org.springframework.web.util.ServletRequestPathUtils.PATH").toString())
            .build();
        return Constructor.buildExceptionResponseMessageObject(
            HttpStatus.valueOf(status.value()), ex.getReason(), apiError);
    }

    @ExceptionHandler(MailSendException.class)
    public final ResponseEntity<?> handleMailSendException(MailSendException ex) {
        String error = "SMTP ";
        if(Objects.requireNonNull(ex.getMessage()).contains("SMTPAddressFailedException: 501 "))
            error += ex.getMessage().split("SMTPAddressFailedException: 501 ")[1];
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .httpStatus(HttpStatus.BAD_REQUEST)
            .clazz("org.ldcgc.backend.util.creation.Email")
            .method("sendRecoveringCredentials")
            .message(error)
            .build();
        return Constructor.buildExceptionResponseObject(HttpStatus.BAD_REQUEST, apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(Messages.Error.UNEXPECTED_ERROR)
            .error(ApiSubError.builder().message(ex.getLocalizedMessage()).build())
            .build();

        return Constructor.buildExceptionResponseObject(HttpStatus.INTERNAL_SERVER_ERROR, apiError);
    }

}
