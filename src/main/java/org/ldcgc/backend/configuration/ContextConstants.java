package org.ldcgc.backend.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ContextConstants {

    @Value("${server.port}")
    private String tomcatPort;

}
