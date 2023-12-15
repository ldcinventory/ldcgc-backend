package org.ldcgc.backend.controller.users;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@Slf4j
@WebMvcTest(controllers = EulaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EulaControllerImplTest {

    @Test
    public void getEula() {

    }

    @Test
    public void putEula() {

    }
}
