package org.ldcgc.backend.controller.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@Slf4j
@WebMvcTest(controllers = AliveController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AliveControllerTest {

    @Test
    void getAlive() {
        fail(NOT_YET_IMPLEMENTED);
    }

}
