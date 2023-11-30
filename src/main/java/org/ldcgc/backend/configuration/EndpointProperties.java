package org.ldcgc.backend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

public class EndpointProperties {

    @Component
    @Configuration
    @EnableConfigurationProperties
    @ConfigurationProperties("exempted")
    @Data
    public static class ExemptedProperties {

        private List<String> GET, POST, PUT, DELETE, PATCH;

    }

    @Component
    @Configuration
    @EnableConfigurationProperties
    @ConfigurationProperties("non-token")
    @Data
    public static class NonTokenProperties {

        private List<String> GET, POST, PUT, DELETE, PATCH;

    }

}
