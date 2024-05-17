package org.ldcgc.backend.service.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.GlobalTestConfig;
import org.ldcgc.backend.service.test.impl.TestServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@ExtendWith({MockitoExtension.class, GlobalTestConfig.class})
public class TestServiceImplTests {

    @InjectMocks private TestServiceImpl testService;

    @Test
    void testAccessWithCredentials() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void testAccessWithManagerCredentials() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void testAccessWithAdminCredentials() {
        fail(NOT_YET_IMPLEMENTED);
    }
}
