package org.ldcgc.backend.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@ExtendWith(MockitoExtension.class)
public class EndpointTests {

    @Test
    void exemptedEndpoint() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void notExemptedEndpoint() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void nonTokenEndpoint() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void isTokenEndpoint() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void isReplaceTokenEndpoint() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void isNotReplaceTokenEndpoint() {
        fail(NOT_YET_IMPLEMENTED);
    }
}
