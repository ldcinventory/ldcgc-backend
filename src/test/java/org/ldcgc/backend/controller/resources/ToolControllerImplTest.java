package org.ldcgc.backend.controller.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.GlobalTestConfig;
import org.ldcgc.backend.controller.resources.impl.ToolControllerImpl;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.tools.ToolService;
import org.ldcgc.backend.util.creation.Constructor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(GlobalTestConfig.class)
@SpringBootTest()
public class ToolControllerImplTest {

    // check different styles of mock or autowire beans in
    // https://www.baeldung.com/spring-boot-testing#mocking-with-mockbean
    @InjectMocks
    private ToolController toolController = new ToolControllerImpl();
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
        ResponseEntity<?> response = toolController.createTool(tool);

        assertTrue(Objects.nonNull(response));
    }

    @Test
    void shouldReturnSameToolDtoBody() {
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(service).createTool(tool);
        ResponseEntity<?> response = toolController.createTool(tool);

        ToolDto toolResponse = objectMapper.convertValue(((Response.DTO) Objects.requireNonNull(response.getBody())).getData(), ToolDto.class);
        assertEquals(tool, toolResponse);
    }

    @Test
    void shouldCallService() {
        ToolDto tool = ToolDto.builder().build();

        toolController.createTool(tool);

        verify(service, times(1)).createTool(tool);
    }
}
