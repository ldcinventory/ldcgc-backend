package org.ldcgc.backend.controller.test;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.exception.ApiError;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.conversion.Convert;
import org.ldcgc.backend.util.creation.Constructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrorController extends AbstractErrorController {

    private final ServerProperties serverProperties;

    public ErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes);
        this.serverProperties = serverProperties;
    }

    @RequestMapping
    public ResponseEntity<?> error(HttpServletRequest request) {
        log.error("Oh no!! An error shown up! :(");

        if (getStatus(request) == HttpStatus.NO_CONTENT)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        Map<String, Object> body = new LinkedHashMap<>();
        //body.put("uuid", getRequestUUIDFromRequestMap(request));
        body.putAll(getErrorAttributes(request, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE)));
        log.error(StringUtils.join(body));

        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", request.getHeader("user-agent"));
        headers.add("host", request.getHeader("host"));

        if(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) instanceof RequestException requestException) {
            ApiError apiError;

            if(requestException.getApiError() != null)
                apiError = requestException.getApiError();
            else
                apiError = ApiError.builder()
                    .httpStatus(Optional.ofNullable(requestException.getHttpStatus()).orElse(HttpStatus.BAD_REQUEST))
                    .status(Optional.ofNullable(requestException.getHttpStatus()).map(HttpStatus::value).orElse(HttpStatus.BAD_REQUEST.value()))
                    .endpoint(MDC.get("requestURI"))
                    .timestamp(Convert.nowToTimeStampString())
                    .message(requestException.getMessage())
                    .build();

            return new ResponseEntity<>(apiError, headers, apiError.getHttpStatus());
        }

        return Constructor.buildResponseObjectHeader(HttpStatus.valueOf((Integer) body.get("status")), body, headers);

    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes, ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new BasicErrorController(errorAttributes, this.serverProperties.getError(),
                errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }

}
