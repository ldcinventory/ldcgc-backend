package org.ldcgc.backend.controller.test;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.app.configuration.ContextConstants;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.app.security.jwt.JwtUtils;
import org.ldcgc.backend.app.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.shared.enums.EUserRole;
import org.ldcgc.backend.shared.validator.UserValidation;
import org.mockito.Mock;
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

import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import static org.ldcgc.backend.base.Authentication.setAuthenticationForRequest;
import static org.ldcgc.backend.base.Constants.API_ROOT;
import static org.ldcgc.backend.base.factory.TestRequestFactory.getRequest;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = AliveController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AliveControllerTest {

    // controller
    @Mock private AliveController aliveController;

    // services
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtUtils jwtUtils;

    // repositories
    @MockBean private UserRepository userRepository;

    // other
    @MockBean private UserValidation userValidation;
    @MockBean private ContextConstants contextConstants;

    private final String requestRoot = "/alive";

    private MockMvc mockMvc;

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
            .standaloneSetup(aliveController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setValidator(validatorFactoryBean)
            .setHandlerExceptionResolvers()
            .build();

        setAuthenticationForRequest(jwtUtils, userRepository, userValidation);

    }

    @Test
    void getAlive() throws Exception {
        final String request = requestRoot;
        final String messageAlive = "Everything OK!";

        log.info("Testing a GET Request to %s%s\n".formatted(API_ROOT, request));

        given(aliveController.getAlive()).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(messageAlive)
        );

        mockMvc.perform(getRequest(request, EUserRole.ROLE_NULL))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(messageAlive))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

}
