package org.ldcgc.backend.controller.resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.GlobalTestConfig;
import org.ldcgc.backend.controller.resources.impl.ToolControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(GlobalTestConfig.class)
@SpringBootTest(classes = ToolControllerImpl.class)
public class ToolControllerImplTest {

    // check different styles of mock or autowire beans in
    // https://www.baeldung.com/spring-boot-testing#mocking-with-mockbean
    @MockBean
    private ToolController toolController;

    @Test
    void shouldReturnTool(){

        ResponseEntity<?> tool = toolController.createTool();
        assertTrue(Objects.nonNull(tool));

    }
}
