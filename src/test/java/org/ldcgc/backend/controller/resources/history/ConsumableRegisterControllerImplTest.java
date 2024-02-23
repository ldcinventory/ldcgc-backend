package org.ldcgc.backend.controller.resources.history;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.controller.history.ConsumableRegisterController;
import org.ldcgc.backend.db.repository.history.ConsumableRegisterRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.history.ConsumableRegisterService;
import org.ldcgc.backend.validator.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import static org.ldcgc.backend.base.Authentication.setAuthenticationForRequest;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomConsumableRegisterDto;

@Slf4j
@WebMvcTest(controllers = ConsumableRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ConsumableRegisterControllerImplTest {

    // controller
    @Autowired private ConsumableRegisterController consumableRegisterController;

    // services
    @MockBean private ConsumableRegisterService consumableRegisterService;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtUtils jwtUtils;

    // repositories
    @MockBean private TokenRepository tokenRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private ConsumableRepository consumableRepository;
    @MockBean private ConsumableRegisterRepository consumableRegisterRepository;

    // other
    @MockBean private UserValidation userValidation;
    @MockBean private ContextConstants contextConstants;

    // context
    @Autowired private WebApplicationContext context;

    // mapper
    @Autowired private ObjectMapper mapper;

    private final String requestRoot = "/resources/consumables/register";

    private MockMvc mockMvc;
    private ConsumableRegisterDto consumableRegisterDto;

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
            .standaloneSetup(consumableRegisterController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setValidator(validatorFactoryBean)
            .setHandlerExceptionResolvers()
            .build();

        consumableRegisterDto = getRandomConsumableRegisterDto();

        setAuthenticationForRequest(jwtUtils, userRepository, userValidation);

    }

    @Test
    void getConsumable() {

    }

    @Test
    void listConsumableRegister() {

    }

    @Test
    void createConsumableRegister() {

    }

    @Test
    void updateConsumableRegister() {

    }

    @Test
    void deleteConsumableRegister() {

    }

}
