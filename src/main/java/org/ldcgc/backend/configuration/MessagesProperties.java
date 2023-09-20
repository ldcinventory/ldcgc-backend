package org.ldcgc.backend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("messages")
@Data
public class MessagesProperties {

    private Map<String, String> info;
    private Map<String, String> app;
    private Map<String, String> errors;

}
