package org.ldcgc.backend.controller.test;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.exception.ApiError;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ErrorControllerTest {

    private ErrorController errorController;
    @Mock ErrorController mockedErrorController;

    private final PodamFactory factory = new PodamFactoryImpl();

    @BeforeEach
    public void init() {
        ErrorAttributes errorAttributes = mock(ErrorAttributes.class);
        ServerProperties serverProperties = factory.manufacturePojo(ServerProperties.class);
        errorController = new ErrorController(errorAttributes, serverProperties);
    }

    @Test
    void errorNoContent() {
        MockHttpServletRequest mockedRequest = new MockHttpServletRequest();
        mockedRequest.setAttribute("jakarta.servlet.error.status_code", 204);

        ResponseEntity<?> responseEntity = errorController.error(mockedRequest);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    void errorApiErrorNotNull() {
        MockHttpServletRequest mockedRequest = new MockHttpServletRequest();
        mockedRequest.setAttribute("jakarta.servlet.error.status_code", 400);
        mockedRequest.setRequestURI("/api/login");
        ApiError requestExceptionApiError = ApiError.builder()
            .status(400)
            .httpStatus(HttpStatus.BAD_REQUEST)
            .message(Messages.Error.USER_NOT_FOUND)
            .endpoint(mockedRequest.getRequestURI())
            .clazz("org.ldcgc.backend.service.users.UserController")
            .build();
        mockedRequest.setAttribute(RequestDispatcher.ERROR_EXCEPTION, new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.USER_NOT_FOUND, requestExceptionApiError));

        MDC.put("requestURI", mockedRequest.getRequestURI());

        ResponseEntity<?> responseEntity = errorController.error(mockedRequest);
        ApiError apiError = (ApiError) responseEntity.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(apiError);
        assertEquals(Messages.Error.USER_NOT_FOUND, apiError.getMessage());
        assertEquals(mockedRequest.getRequestURI(), apiError.getEndpoint());

    }

    @Test
    void errorApiErrorNull() {
        MockHttpServletRequest mockedRequest = new MockHttpServletRequest();
        mockedRequest.setAttribute("jakarta.servlet.error.status_code", 400);
        mockedRequest.setRequestURI("/api/login");
        mockedRequest.setAttribute(RequestDispatcher.ERROR_EXCEPTION, new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.USER_NOT_FOUND));

        MDC.put("requestURI", mockedRequest.getRequestURI());

        ResponseEntity<?> responseEntity = errorController.error(mockedRequest);
        ApiError apiError = (ApiError) responseEntity.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(apiError);
        assertEquals(Messages.Error.USER_NOT_FOUND, apiError.getMessage());
        assertEquals(mockedRequest.getRequestURI(), apiError.getEndpoint());

    }

    @Test
    void errorNotRequestException() {
        MockHttpServletRequest mockedRequest = new MockHttpServletRequest();
        mockedRequest.setAttribute("jakarta.servlet.error.status_code", 500);
        mockedRequest.setRequestURI("/api/login");
        mockedRequest.setAttribute(RequestDispatcher.ERROR_EXCEPTION, new NullPointerException("java.lang.NullPointerException;"));

        MDC.put("requestURI", mockedRequest.getRequestURI());

        Map<String, Object> mockedRequestBody = new LinkedHashMap<>(
            Map.of(
                "timestamp", new Date(),
                "status", 500,
                "error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "message", Messages.Error.TOKEN_NOT_VALID,
                "path", Objects.requireNonNull(mockedRequest.getRequestURI())
            ));

        ReflectionTestUtils.setField(mockedErrorController, "serverProperties", mock(ServerProperties.class));
        ReflectionTestUtils.setField(mockedErrorController, "errorAttributes", mock(ErrorAttributes.class));

        doReturn(mockedRequestBody).when(mockedErrorController).getErrorAttributes(any(HttpServletRequest.class), any(ErrorAttributeOptions.class));

        ResponseEntity<?> responseEntity = mockedErrorController.error(mockedRequest);
        ApiError apiError = (ApiError) responseEntity.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(apiError);
        assertEquals(Messages.Error.TOKEN_NOT_VALID, apiError.getMessage());
        assertEquals(mockedRequest.getRequestURI(), apiError.getEndpoint());

    }

}
