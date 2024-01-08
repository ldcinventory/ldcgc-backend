package org.ldcgc.backend.validator;

import org.ldcgc.backend.configuration.EndpointProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Endpoint {

    private static final Map<String, List<String>> exempted = new HashMap<>();
    private static final Map<String, List<String>> nonToken = new HashMap<>();
    private static final Map<String, List<String>> replaceToken = new HashMap<>();

    private Endpoint(EndpointProperties.ExemptedProperties exemptedEndpointProperties,
                     EndpointProperties.NonTokenProperties nonTokenProperties,
                     EndpointProperties.ReplaceTokenProperties replaceTokenProperties) {

        exempted.putAll(Map.of(
            "GET", exemptedEndpointProperties.getGET(),
            "POST", exemptedEndpointProperties.getPOST(),
            "PUT", exemptedEndpointProperties.getPUT(),
            "PATCH", exemptedEndpointProperties.getPATCH(),
            "DELETE", exemptedEndpointProperties.getDELETE()
        ));

        nonToken.putAll(Map.of(
            "GET", nonTokenProperties.getGET(),
            "POST", nonTokenProperties.getPOST(),
            "PUT", nonTokenProperties.getPUT(),
            "PATCH", nonTokenProperties.getPATCH(),
            "DELETE", nonTokenProperties.getDELETE()
        ));

        replaceToken.putAll(Map.of(
            "GET", replaceTokenProperties.getGET(),
            "POST", replaceTokenProperties.getPOST(),
            "PUT", replaceTokenProperties.getPUT(),
            "PATCH", replaceTokenProperties.getPATCH(),
            "DELETE", replaceTokenProperties.getDELETE()
        ));

    }

    public static boolean exemptedEndpoint(String method, String endpoint) {
        return exempted.get(method).contains(endpoint);
    }

    public static boolean notExemptedEndpoint(String method, String endpoint) {
        return !exemptedEndpoint(method, endpoint);
    }

    public static boolean nonTokenEndpoint(String method, String endpoint) {
        return nonToken.get(method).contains(endpoint);
    }

    public static boolean isTokenEndpoint(String method, String endpoint) {
        return !nonTokenEndpoint(method, endpoint);
    }

    public static boolean isReplaceTokenEndpoint(String method, String endpoint) {
        return replaceToken.get(method).contains(endpoint);
    }

    public static boolean isNotReplaceTokenEndpoint(String method, String endpoint) {
        return !isReplaceTokenEndpoint(method, endpoint);
    }

}
