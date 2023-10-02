package org.ldcgc.backend.controller.resources.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.controller.resources.tool.impl.ToolControllerImpl;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
public class ToolControllerImplTest {

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
    void shouldReturnResponseEntity() {
        ToolDto tool = ToolDto.builder().build();

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(service).createTool(tool);
        ResponseEntity<?> response = controller.createTool(tool);

        assertTrue(Objects.nonNull(response));
    }

    @Test
    void shouldReturnSameToolDtoBody() {
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(service).createTool(tool);
        ResponseEntity<?> response = controller.createTool(tool);

        ToolDto toolResponse = objectMapper.convertValue(((Response.DTO) Objects.requireNonNull(response.getBody())).getData(), ToolDto.class);
        assertEquals(tool, toolResponse);
    }

    @Test
    void shouldCallService() {
        ToolDto tool = ToolDto.builder().build();

        controller.createTool(tool);

        verify(service, times(1)).createTool(tool);
    }
}
