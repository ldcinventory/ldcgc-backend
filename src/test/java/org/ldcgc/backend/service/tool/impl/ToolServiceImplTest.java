package org.ldcgc.backend.service.tool.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.GlobalTestConfig;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.service.resources.tool.impl.ToolServiceImpl;
import org.ldcgc.backend.transformer.resources.tool.ToolTransformer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(GlobalTestConfig.class)
@SpringBootTest
class ToolServiceImplTest {

    @InjectMocks
    private ToolService service = new ToolServiceImpl();

    @Mock
    private ToolTransformer transformer;
    @Mock
    private ToolRepository repository;

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
