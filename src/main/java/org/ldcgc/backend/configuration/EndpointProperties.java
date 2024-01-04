package org.ldcgc.backend.configuration;

import lombok.Getter;
import lombok.Setter;
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
    public static class ExemptedProperties {

        @Getter @Setter
        private List<String> GET, POST, PUT, DELETE, PATCH;

    }

    @Component
    @Configuration
    @EnableConfigurationProperties
    @ConfigurationProperties("non-token")
    public static class NonTokenProperties {

        @Getter @Setter
        private List<String> GET, POST, PUT, DELETE, PATCH;

    }

    @Component
    @Configuration
    @EnableConfigurationProperties
    @ConfigurationProperties("replace-token")
    public static class ReplaceTokenProperties {

        @Getter @Setter
        private List<String> GET, POST, PUT, DELETE, PATCH;

    }
}
