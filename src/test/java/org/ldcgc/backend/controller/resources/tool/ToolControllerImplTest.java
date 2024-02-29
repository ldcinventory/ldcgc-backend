package org.ldcgc.backend.controller.resources.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.controller.resources.ToolController;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.retrieving.Messages;
import org.ldcgc.backend.validator.UserValidation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.ldcgc.backend.base.factory.TestRequestFactory.postRequest;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = ToolController.class)
@AutoConfigureMockMvc(addFilters = false)
class ToolControllerImplTest {

    // controller
    @Autowired private ToolController toolController;

    // service
    @MockBean private ToolService toolService;

    // authentication
    @MockBean private JwtUtils jwtUtils;
    @MockBean private UserRepository userRepository;
    @MockBean private UserValidation userValidation;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private TokenRepository tokenRepository;

    // other
    @MockBean private ContextConstants contextConstants;
    @Autowired private ObjectMapper mapper;

    private final PodamFactory factory = new PodamFactoryImpl();
    private final String requestRoot = "/resources/tools";

    private MockMvc mockMvc;

    @BeforeEach
    void init() throws ParseException {
        final GenericWebApplicationContext context = new GenericWebApplicationContext(new MockServletContext());
        final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
        beanFactory.registerSingleton(UserValidation.class.getCanonicalName(), UserValidation.bean(userRepository, jwtUtils));
        context.refresh();

        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setApplicationContext(context);
        validatorFactoryBean.setConstraintValidatorFactory(new TestConstrainValidationFactory(context));
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(toolController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .setValidator(validatorFactoryBean)
                .setHandlerExceptionResolvers()
                .build();

        setAuthenticationForRequest();

    }

    @Test
    void postShouldReturnResponseEntity() throws Exception {
        ToolDto tool = ToolDto.builder().build();
        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.TOOL_CREATED).data(tool).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        doReturn(response).when(toolService).createTool(tool);

        mockMvc.perform(postRequest(requestRoot, ERole.ROLE_ADMIN)
                    .content(mapper.writeValueAsString(tool)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andExpect(content().encoding(StandardCharsets.UTF_8));

        /*
        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(service).createTool(tool);
        ResponseEntity<?> response = controller.createTool(tool);

        verify(service, times(1)).createTool(tool);

        assertTrue(Objects.nonNull(response));*/
        assertNotNull(response);
    }

    @Test
    void postShouldReturnSameToolDtoBody() {
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(toolService).createTool(tool);
        ResponseEntity<?> response = toolController.createTool(tool);

        assertEquals(tool, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData());
    }

    @Test
    void getShouldReturnToolDtoBody() {
        Integer toolId = factory.manufacturePojo(Integer.class);
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(toolService).getTool(toolId);
        ResponseEntity<?> response = toolController.getTool(toolId);

        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
        verify(toolService, times(1)).getTool(toolId);
    }

    @Test
    void putShouldReturnToolDtoBody() {
        Integer toolId = factory.manufacturePojo(Integer.class);
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(toolService).updateTool(toolId, tool);
        ResponseEntity<?> response = toolController.updateTool(toolId, tool);

        verify(toolService, times(1)).updateTool(toolId, tool);
        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }

    @Test
    void deleteShouldReturnToolDtoBody() {
        Integer toolId = factory.manufacturePojo(Integer.class);
        ToolDto tool = factory.manufacturePojo(ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tool).build())).when(toolService).deleteTool(toolId);
        ResponseEntity<?> response = toolController.deleteTool(toolId);

        verify(toolService, times(1)).deleteTool(toolId);
        assertEquals(ToolDto.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
    }

    @Test
    void getAllShouldCallService() {
        toolController.getAllTools(0, 25, "name", "", "", "", null);

        verify(toolService, times(1)).getAllTools(0, 25, "name", "", "", "", null);
    }

    @Test
    void uploadToolsExcelShouldCallService(){
        MultipartFile file = factory.manufacturePojo(MultipartFile.class);
        toolController.uploadToolsExcel(file);
        verify(toolService, times(1)).uploadToolsExcel(file);
    }

    @Test
    void getAllShouldReturnToolList() {
        List<ToolDto> tools = factory.manufacturePojo(ArrayList.class, ToolDto.class);

        doReturn(ResponseEntity.ok(Response.DTO.builder().data(tools).build())).when(toolService).getAllTools(0, 0, "name", "", "", "", null);
        ResponseEntity<?> response = toolController.getAllTools(0, 0, "name", "", "", "", null);

        verify(toolService, times(1)).getAllTools(0, 0, "name", "", "", "", null);
        assertEquals(ArrayList.class, ((Response.DTO) Objects.requireNonNull(response.getBody())).getData().getClass());
        assertEquals(ToolDto.class, ((List<ToolDto>)((Response.DTO) Objects.requireNonNull(response.getBody())).getData()).getFirst().getClass());
    }

    private void setAuthenticationForRequest() throws ParseException {
        given(jwtUtils.getUserIdFromStringToken(Mockito.anyString())).willReturn(0);
        given(userRepository.existsById(Mockito.anyInt())).willReturn(Boolean.TRUE);
        given(userValidation.userFromTokenExistsInDB(Mockito.anyString())).willReturn(Boolean.TRUE);
    }

}
