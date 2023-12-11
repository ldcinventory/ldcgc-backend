package org.ldcgc.backend.base.factory;

import org.ldcgc.backend.util.common.ERole;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.ldcgc.backend.base.mock.MockedToken.generateNewStringToken;
import static org.ldcgc.backend.base.mock.MockedUserDetails.getRandomMockedUser;

public class TestRequestFactory {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static String mockedToken;

    private static MockHttpServletRequestBuilder defaultRequestValues(MockHttpServletRequestBuilder requestBuilder, ERole tokenUserRole) {

        if(tokenUserRole == null)
            tokenUserRole = ERole.ROLE_USER;

        return requestBuilder
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .requestAttr("Authorization", generateNewStringToken(getRandomMockedUser(tokenUserRole)));
    }

    public static MockHttpServletRequestBuilder getRequest(String url, ERole tokenUserRole) {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(url);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder getRequest(String url) {
        return getRequest(url, null);
    }

    public static MockHttpServletRequestBuilder postRequest(String url, ERole tokenUserRole) {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(url);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder postRequest(String url) {
        return postRequest(url, null);
    }

    public static MockHttpServletRequestBuilder putRequest(String url, ERole tokenUserRole) {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(url);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder putRequest(String url) {
        return putRequest(url, null);
    }

    public static MockHttpServletRequestBuilder patchRequest(String url, ERole tokenUserRole) {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(url);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder patchRequest(String url) {
        return patchRequest(url, null);
    }

    public static MockHttpServletRequestBuilder deleteRequest(String url, ERole tokenUserRole) {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(url);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder deleteRequest(String url) {
        return deleteRequest(url, null);
    }
}
