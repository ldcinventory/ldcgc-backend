package org.ldcgc.backend.base.factory;

import org.ldcgc.backend.util.common.ERole;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.ldcgc.backend.base.mock.MockedToken.generateNewStringToken;
import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getRandomMockedUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class TestRequestFactory {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static MockHttpServletRequestBuilder defaultRequestValues(MockHttpServletRequestBuilder requestBuilder, ERole tokenUserRole) {

        if(tokenUserRole == null)
            return requestBuilder
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(user("user").roles("USER"));

        return requestBuilder
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .requestAttr(AUTHORIZATION_HEADER, Objects.requireNonNull(generateNewStringToken(getRandomMockedUser(tokenUserRole))))
            .with(user(tokenUserRole.getRoleName().toLowerCase()).roles(tokenUserRole.getRoleName()));
    }

    // get

    public static MockHttpServletRequestBuilder getRequest(String url, ERole tokenUserRole, Object... uriVariables) {
        MockHttpServletRequestBuilder request = uriVariables == null ? get(url) : get(url, uriVariables);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder getRequest(String url, ERole tokenUserRole) {
        return getRequest(url, tokenUserRole, (Object[]) null);
    }

    public static MockHttpServletRequestBuilder getRequest(String url, Object... uriVariables) {
        return getRequest(url, null, uriVariables);
    }

    public static MockHttpServletRequestBuilder getRequest(String url) {
        return getRequest(url, null, (Object[]) null);
    }

    // post

    public static MockHttpServletRequestBuilder postRequest(String url, ERole tokenUserRole, Object... uriVariables) {
        MockHttpServletRequestBuilder request = uriVariables == null ? post(url) : post(url, uriVariables);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder postRequest(String url, ERole tokenUserRole) {
        return postRequest(url, tokenUserRole, (Object[]) null);
    }

    public static MockHttpServletRequestBuilder postRequest(String url, Object ...uriVariables) {
        return postRequest(url, null, uriVariables);
    }

    public static MockHttpServletRequestBuilder postRequest(String url) {
        return postRequest(url, null, (Object[]) null);
    }

    public static MockHttpServletRequestBuilder postMultipartRequest(String url, MockMultipartFile file, ERole tokenUserRole) {

        return MockMvcRequestBuilders.multipart(url)
            .file(file)
            .with(request -> {
                request.setMethod("POST");
                request.addUserRole(tokenUserRole.getRoleName().toLowerCase());
                request.addUserRole(tokenUserRole.getRoleName());
                return request;
            })
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8);

    }

    // put

    public static MockHttpServletRequestBuilder putRequest(String url, ERole tokenUserRole, Object... uriVariables) {
        MockHttpServletRequestBuilder request = uriVariables == null ? put(url) : put(url, uriVariables);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder putRequest(String url, ERole tokenUserRole) {
        return putRequest(url, tokenUserRole, (Object[]) null);
    }

    public static MockHttpServletRequestBuilder putRequest(String url, Object ...uriVariables) {
        return putRequest(url, null, uriVariables);
    }

    public static MockHttpServletRequestBuilder putRequest(String url) {
        return putRequest(url, null, (Object[]) null);
    }

    // patch

    public static MockHttpServletRequestBuilder patchRequest(String url, ERole tokenUserRole, Object... uriVariables) {
        MockHttpServletRequestBuilder request = uriVariables == null ? patch(url) : patch(url, uriVariables);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder patchRequest(String url, ERole tokenUserRole) {
        return patchRequest(url, tokenUserRole, (Object[]) null);
    }

    public static MockHttpServletRequestBuilder patchRequest(String url, Object ...uriVariables) {
        return patchRequest(url, null, uriVariables);
    }

    public static MockHttpServletRequestBuilder patchRequest(String url) {
        return patchRequest(url, null, (Object[]) null);
    }

    // delete

    public static MockHttpServletRequestBuilder deleteRequest(String url, ERole tokenUserRole, Object... uriVariables) {
        MockHttpServletRequestBuilder request = uriVariables == null ? delete(url) : delete(url, uriVariables);
        return defaultRequestValues(request, tokenUserRole);
    }

    public static MockHttpServletRequestBuilder deleteRequest(String url, ERole tokenUserRole) {
        return deleteRequest(url, tokenUserRole, (Object[]) null);
    }

    public static MockHttpServletRequestBuilder deleteRequest(String url, Object ...uriVariables) {
        return deleteRequest(url, null, uriVariables);
    }

    public static MockHttpServletRequestBuilder deleteRequest(String url) {
        return deleteRequest(url, null, (Object[]) null);
    }
}
