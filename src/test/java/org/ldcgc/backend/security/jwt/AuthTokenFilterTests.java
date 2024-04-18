package org.ldcgc.backend.security.jwt;

import org.junit.jupiter.api.Test;
import org.ldcgc.backend.exception.RequestException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

public class AuthTokenFilterTests {

    @Test
    public void doFilterInternalNormal() {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalRefresh() {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalAuthIsNotPresent() {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalNonTokenEndpoint() {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalBlankJwt() throws RequestException {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalCantVerifyJwt() throws RequestException {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalNotExemptedEndpointNotEula() throws RequestException {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalNotExemptedEndpointNotEulaManager() throws RequestException {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalNotExemptedEndpointNotEulaAdmin() throws RequestException {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalParseException() throws RequestException {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalJOSEException() throws RequestException {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalIllegalArgumentException() throws RequestException {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalNullPointerException() throws RequestException {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalSetExpirationTimeFromDecodedJWT() {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void doFilterInternalSetExpirationTimeFromLocalDateTime() {
        fail(NOT_YET_IMPLEMENTED);

    }

}
