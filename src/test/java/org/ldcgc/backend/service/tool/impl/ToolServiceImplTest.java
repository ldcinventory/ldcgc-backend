package org.ldcgc.backend.service.tool.impl;

import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.tool.impl.ToolServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ToolServiceImplTest {

    @InjectMocks
    private ToolServiceImpl service;

    @Mock
    private ToolRepository repository;

    private final PodamFactory factory = new PodamFactoryImpl();


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

        doReturn(tool).when(repository).save(tool);

        ResponseEntity<?> response = service.updateTool(tool.getId(), toolDto);

        verify(repository, times(1)).save(tool);
        assertTrue(Objects.nonNull(response));
        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }

    @Test
    void deleteToolShouldReturnResponseEntity() {
        Tool tool = factory.manufacturePojo(Tool.class);
        Integer toolId = tool.getId();

        doReturn(Optional.of(tool)).when(repository).findById(toolId);

        ResponseEntity<?> response = service.deleteTool(toolId);

        verify(repository, times(1)).findById(toolId);
        verify(repository, times(1)).delete(tool);

        assertTrue(Objects.nonNull(response));
        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }

    @Test()
    void deleteToolShouldThrowErrorWhenToolNotFound() {
        Tool tool = factory.manufacturePojo(Tool.class);
        Integer toolId = tool.getId();

        doReturn(Optional.empty()).when(repository).findById(toolId);

        assertThrows(RequestException.class, () -> service.deleteTool(toolId));

        verify(repository, times(1)).findById(toolId);
        verify(repository, times(0)).delete(tool);
    }

    //TODO: Fix test and add excel tests, get all pageable tests
 /*   @Test
    void getAllToolsShouldReturnList() {
        List<Tool> tools = factory.manufacturePojo(ArrayList.class, Tool.class);

        doReturn(tools).when(repository).findAll();

        ResponseEntity<?> response = service.getAllTools(0, 0, "");

        verify(repository, times(1)).findAll();

        assertTrue(Objects.nonNull(response));
        assertEquals("java.util.ImmutableCollections$ListN", ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass().getName());
        assertEquals(ToolDto.class, ((Page<ToolDto>)((Response.DTO) Objects.requireNonNull(response.getBody())).getData()).get().findFirst().getClass());
    }*/

}
