package org.ldcgc.backend.controller.history;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.controller.resources.ConsumableController;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.service.history.ToolRegisterService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@Slf4j
@WebMvcTest(controllers = ToolRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ToolRegisterControllerImplTest {

    @Autowired private ToolRegisterController toolRegisterController;

    @MockBean private ToolRegisterService service;

    private final PodamFactory factory = new PodamFactoryImpl();
    @Test
    void whenCreateCalled_shouldCallService(){
        ToolRegisterDto toolRegisterDto = factory.manufacturePojo(ToolRegisterDto.class);

        doReturn(Constructor.buildResponseMessageObject(HttpStatus.CREATED,
                Messages.Info.TOOL_REGISTER_CREATED,
                toolRegisterDto)).when(service).createToolRegister(toolRegisterDto);

        ResponseEntity<?> response = toolRegisterController.createToolRegister(toolRegisterDto);

        verify(service, atMostOnce()).createToolRegister(toolRegisterDto);
        assertEquals(ToolRegisterDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }
}
