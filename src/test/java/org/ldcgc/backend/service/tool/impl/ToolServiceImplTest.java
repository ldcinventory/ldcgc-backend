package org.ldcgc.backend.service.tool.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.tool.impl.ToolServiceImpl;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
class ToolServiceImplTest {

    @InjectMocks
    private ToolServiceImpl service;

    @Mock
    private ToolRepository repository;

    private PodamFactory factory = new PodamFactoryImpl();
    @Test
    void createToolShouldReturnResponseEntity() {
        ToolDto toolDto = ToolDto.builder().build();
        Tool entityTool = ToolMapper.MAPPER.toMo(toolDto);

        doReturn(entityTool).when(repository).save(entityTool);

        ResponseEntity<?> response = service.createTool(toolDto);

        assertTrue(Objects.nonNull(response));
        verify(repository, times(1)).save(entityTool);
        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }

    @Test
    void getToolShouldReturnResponseEntity() {
        Tool tool = factory.manufacturePojo(Tool.class);
        ToolDto toolDto = ToolMapper.MAPPER.toDto(tool);

        doReturn(Optional.of(tool)).when(repository).findById(tool.getId());

        ResponseEntity<?> response = service.getTool(tool.getId());

        assertTrue(Objects.nonNull(response));
        verify(repository, times(1)).findById(tool.getId());
        assertEquals(toolDto, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData());
        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }

    @Test
    void putToolShouldReturnResponseEntity() {
        ToolDto toolDto = factory.manufacturePojo(ToolDto.class);
        Tool tool = ToolMapper.MAPPER.toMo(toolDto);

        //doReturn(Optional.of(tool)).when(repository).findById(tool.getId());
        doReturn(tool).when(repository).save(tool);

        ResponseEntity<?> response = service.updateTool(tool.getId(), toolDto);

        //verify(repository, times(1)).findById(toolDto.getId());
        verify(repository, times(1)).save(tool);
        assertTrue(Objects.nonNull(response));
        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }
/*
    @Test
    void*/
}
