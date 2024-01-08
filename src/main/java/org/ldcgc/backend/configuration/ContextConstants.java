package org.ldcgc.backend.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class ContextConstants {

    @Value("${server.port}")
    private String tomcatPort;

}
