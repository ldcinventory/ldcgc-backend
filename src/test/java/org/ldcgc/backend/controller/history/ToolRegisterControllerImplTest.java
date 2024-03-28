package org.ldcgc.backend.controller.history;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.db.repository.history.ToolRegisterRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.history.ToolRegisterService;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.validator.UserValidation;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.ldcgc.backend.base.Authentication.setAuthenticationForRequest;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomConsumableDto;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@Slf4j
@WebMvcTest(controllers = ToolRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ToolRegisterControllerImplTest {

    private final PodamFactory factory = new PodamFactoryImpl();

    // controller
    @Autowired private ToolRegisterController toolRegisterController;

    // services
    @MockBean private ToolService toolService;
    @MockBean private ToolRegisterService service;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtUtils jwtUtils;

    // repositories
    @MockBean private TokenRepository tokenRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private ToolRegisterRepository repository;
    @MockBean private VolunteerRepository volunteerRepository;
    @MockBean private ToolRepository toolRepository;

    // other
    @MockBean private UserValidation userValidation;
    @MockBean private ContextConstants contextConstants;

    // context
    @Autowired private WebApplicationContext context;

    // mapper
    @Autowired private ObjectMapper mapper;


    private final String requestRoot = "/resources/tools/registers";

    private MockMvc mockMvc;
    private ConsumableDto consumableDto;

    @BeforeEach
    public void init() throws ParseException {

        final GenericWebApplicationContext context = new GenericWebApplicationContext(new MockServletContext());
        final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
        beanFactory.registerSingleton(UserValidation.class.getCanonicalName(), UserValidation.bean(userRepository, jwtUtils));
        context.refresh();

        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setApplicationContext(context);
        TestConstrainValidationFactory constrainValidationFactory = new TestConstrainValidationFactory(context);
        validatorFactoryBean.setConstraintValidatorFactory(constrainValidationFactory);
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders
            .standaloneSetup(toolRegisterController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setValidator(validatorFactoryBean)
            .setHandlerExceptionResolvers()
            .build();

        consumableDto = getRandomConsumableDto();

        setAuthenticationForRequest(jwtUtils, userRepository, userValidation);

    }

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
