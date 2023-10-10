package org.ldcgc.backend.service.tool.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.tool.impl.ToolServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
class ToolServiceImplTest {

    @InjectMocks
    private ToolServiceImpl service;

    @Mock
    private ToolRepository repository;

    @Test
    void createToolShouldReturnResponseEntity() {
        ResponseEntity<?> response = service.createTool(ToolDto.builder().build());

        assertTrue(Objects.nonNull(response));
    }

    @Test
    void createToolShouldCallTransformer(){
        try (MockedStatic<ToolMapper> mocked = Mockito.mockStatic(ToolMapper.class)) {
            ToolDto tool = ToolDto.builder().build();

            service.createTool(tool);
            mocked.verify(() -> ToolMapper.MAPPER.toMo(any(ToolDto.class)), times(1));
        }
    }

    @Test
    void createToolShouldCallRepository(){
        ToolDto tool = ToolDto.builder().build();
        Tool entityTool = ToolMapper.MAPPER.toMo(tool);

        service.createTool(tool);

        verify(repository, times(1)).save(entityTool);
    }
}
