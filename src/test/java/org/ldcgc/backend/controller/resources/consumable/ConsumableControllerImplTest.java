package org.ldcgc.backend.controller.resources.consumable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.controller.resources.ConsumableController;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.users.UserService;
import org.ldcgc.backend.validator.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.ldcgc.backend.base.mock.MockedResources.getRandomConsumableDto;

@SpringBootTest
public class ConsumableControllerImplTest {

    // controller
    @Autowired private ConsumableController consumableController;

    // services
    @MockBean private UserService userService;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtUtils jwtUtils;

    // repositories
    @MockBean private TokenRepository tokenRepository;
    @MockBean private UserRepository userRepository;

    // other
    @MockBean private UserValidation userValidation;
    @MockBean private ContextConstants contextConstants;

    // context
    @Autowired private WebApplicationContext context;

    // mapper
    @Autowired private ObjectMapper mapper;

    private final String requestRoot = "/users";

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
        validatorFactoryBean.setConstraintValidatorFactory(new TestConstrainValidationFactory(context));
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders
            .standaloneSetup(consumableController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setValidator(validatorFactoryBean)
            .setHandlerExceptionResolvers()
            .build();

        consumableDto = getRandomConsumableDto();

        setAuthenticationForRequest(jwtUtils, userRepository, userValidation);

    }


    //TODO: HACER LOS TESTS DEL CONTROLLER CON COBERTURA DE 75% O MÁS
    @Test
    void getConsumable() {

    }

    @Test
    void createConsumable() {

    }

    @Test
    void updateConsumable() {

    }

    @Test
    void listConsumables() {

    }

    @Test
    void deleteConsumable() {

    }

    @Test
    void loadExcel() {

    }

}
