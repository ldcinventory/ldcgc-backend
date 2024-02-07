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

    // HTTP int status
    public static final String HTTP_200 = "200",
        HTTP_201 = "201",
        HTTP_204 = "204",
        HTTP_400 = "400",
        HTTP_401 = "401",
        HTTP_403 = "403",
        HTTP_404 = "404",
        HTTP_406 = "406",
        HTTP_409 = "409",
        HTTP_422 = "422",
        HTTP_500 = "500",
        HTTP_501 = "501";

    // HTTP message status
    public static final String HTTP_REASON_200 = "OK",
        HTTP_REASON_201 = "Created",
        HTTP_REASON_204 = "No Content",
        HTTP_REASON_400 = "Bad Request",
        HTTP_REASON_401 = "Unauthorized",
        HTTP_REASON_403 = "Forbidden",
        HTTP_REASON_404 = "Not Found",
        HTTP_REASON_406 = "Not Acceptable",
        HTTP_REASON_409 = "Conflict",
        HTTP_REASON_422 = "Unprocessable Entity",
        HTTP_REASON_500 = "Internal Server Error",
        HTTP_REASON_501 = "Not Implemented";

    // Swagger constants
    public static final String SWAGGER_ROLE_OPERATION_ADMIN = "Roles addmited: <b>ADMIN</b>",
        SWAGGER_ROLE_OPERATION_MANAGER = "Roles addmited: <b>ADMIN</b>, <b>MANAGER</b>",
        SWAGGER_ROLE_OPERATION_USER = "Roles addmited: <b>ADMIN</b>, <b>MANAGER</b>, <b>STANDARD</b>",
        SWAGGER_ROLE_OPERATION_NON_LOGGED = "Roles addmited: <b>Non-logged user</b>";

    @Bean
    public OpenAPI gc8InventoryAPI() {
        return new OpenAPI()
                .info(new Info().title("LDC-GC API")
                        .description("LDC-GC Backend")
                        .version(version)
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("LDC Inventory Help")
                        .url("https://help.gc8inventory.es"));
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
