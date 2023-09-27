package org.ldcgc.backend.controller.resources;

import org.junit.jupiter.api.Test;
import org.ldcgc.backend.controller.resources.impl.ToolControllerImpl;
import org.ldcgc.backend.controller.test.impl.TestControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = TestControllerImpl.class)
public class ToolControllerImplTest {
    @Autowired
    ToolControllerImpl controller;

    @Test
    void shouldReturnTool(){
        ResponseEntity<?> tool = controller.createTool();

        assertTrue(Objects.nonNull(tool));
    }
}
