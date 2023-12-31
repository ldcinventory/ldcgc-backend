package org.ldcgc.backend.controller.resources.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.controller.resources.ToolController;
import org.ldcgc.backend.controller.resources.impl.ToolControllerImpl;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ToolControllerImplTest {

    @InjectMocks
    private ToolController controller = new ToolControllerImpl();
    private final PodamFactory factory = new PodamFactoryImpl();
    static ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private ToolService service;

    @BeforeAll
    static void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void postShouldReturnResponseEntity() {
        ToolDto tool = ToolDto.builder().build();

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(service).createTool(tool);
        ResponseEntity<?> response = controller.createTool(tool);

        verify(service, times(1)).createTool(tool);
        assertTrue(Objects.nonNull(response));
    }

    @Test
    void postShouldReturnSameToolDtoBody() {
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(service).createTool(tool);
        ResponseEntity<?> response = controller.createTool(tool);

        assertEquals(tool, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData());
    }

    @Test
    void getShouldReturnToolDtoBody() {
        Integer toolId = factory.manufacturePojo(Integer.class);
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(service).getTool(toolId);
        ResponseEntity<?> response = controller.getTool(toolId);

        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
        verify(service, times(1)).getTool(toolId);
    }

    @Test
    void putShouldReturnToolDtoBody() {
        Integer toolId = factory.manufacturePojo(Integer.class);
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(service).updateTool(toolId, tool);
        ResponseEntity<?> response = controller.updateTool(toolId, tool);

        verify(service, times(1)).updateTool(toolId, tool);
        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }

    @Test
    void deleteShouldReturnToolDtoBody() {
        Integer toolId = factory.manufacturePojo(Integer.class);
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(service).deleteTool(toolId);
        ResponseEntity<?> response = controller.deleteTool(toolId);

        verify(service, times(1)).deleteTool(toolId);
        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }

    @Test
    void getAllShouldCallService() {
        controller.getAllTools(0, 25, "name", "", "", "", null);

        verify(service, times(1)).getAllTools(0, 25, "name", "", "", "", null);
    }

    @Test
    void uploadToolsExcelShouldCallService(){
        MultipartFile file = factory.manufacturePojo(MultipartFile.class);
        controller.uploadToolsExcel(file);
        verify(service, times(1)).uploadToolsExcel(file);
    }

    @Test
    void getAllShouldReturnToolList() {
        List<ToolDto> tools = factory.manufacturePojo(ArrayList.class, ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tools).build())).when(service).getAllTools(0, 0, "name", "", "", "", null);
        ResponseEntity<?> response = controller.getAllTools(0, 0, "name", "", "", "", null);

        verify(service, times(1)).getAllTools(0, 0, "name", "", "", "", null);
        assertEquals(ArrayList.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
        assertEquals(ToolDto.class, ((List<ToolDto>)((Response.DTO) Objects.requireNonNull(response.getBody())).getData()).getFirst().getClass());
    }


}
