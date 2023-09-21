package org.ldcgc.backend.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${application.version}")
    private String version;

    @Bean
    public OpenAPI ipreachAPI() {
        return new OpenAPI()
                .info(new Info().title("LDC-GC API")
                        .description("LDC-GC Backend")
                        .version(version)
                        .license(new License().name("Apache 2.0").url("https://ldcgc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("LDC Inventory Help")
                        .url("https://help.ipreach.net"));
    }

    private SecurityScheme securityScheme(String name) {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(name);
    }

    /* establishes a security context for endpoints as seen on
    // https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope[] scopes = {
                new AuthorizationScope("admin", "access everything (only admin)"),
                new AuthorizationScope("manager", "access manager endpoints"),
                new AuthorizationScope("professional", "access professional user endpoints"),
                new AuthorizationScope("standard", "access standard user endpoints"),
                new AuthorizationScope("anonymous", "access non-required authorization endpoints")
        };

        return Lists.newArrayList(new SecurityReference(jwtToken, scopes));
    }
    */

}
