package org.ldcgc.backend.service.tool.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.transformer.resources.tool.ToolMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
class ToolServiceImplTest {

    @InjectMocks
    private ToolService service;

    @Mock
    private ToolMapper transformer;

    @Test
    void createToolShouldReturnResponseEntity() {
        ResponseEntity<?> response = service.createTool(ToolDto.builder().build());

        assertTrue(Objects.nonNull(response));
    }

    @Test
    void createToolShouldCallTransformer(){
        ToolDto tool = ToolDto.builder().build();

        service.createTool(tool);

        verify(transformer, times(1)).toMo(tool);
    }
}
