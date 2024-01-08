package org.ldcgc.backend.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.users.EulaService;
import org.ldcgc.backend.util.common.EEULAStatus;
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

import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import static org.ldcgc.backend.base.Constants.apiRoot;
import static org.ldcgc.backend.base.factory.TestRequestFactory.getRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.putRequest;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = EulaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EulaControllerImplTest {

    // controller
    @Autowired private EulaController eulaController;
    // services
    @MockBean private EulaService eulaService;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtUtils jwtUtils;
    // repositories
    @MockBean private TokenRepository tokenRepository;
    @MockBean private UserRepository userRepository;
    // other
    @MockBean private UserValidation userValidation;
    @MockBean private ContextConstants contextConstants;
    // mapper
    @Autowired private ObjectMapper mapper;

    private final String requestRoot = "/eula";

    private MockMvc mockMvc;
    private TestConstrainValidationFactory constrainValidationFactory;

    @BeforeEach
    public void init() throws ParseException {

        final GenericWebApplicationContext context = new GenericWebApplicationContext(new MockServletContext());
        final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
        beanFactory.registerSingleton(UserValidation.class.getCanonicalName(), UserValidation.bean(userRepository, jwtUtils));
        context.refresh();

        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setApplicationContext(context);
        constrainValidationFactory = new TestConstrainValidationFactory(context);
        validatorFactoryBean.setConstraintValidatorFactory(constrainValidationFactory);
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders
            .standaloneSetup(eulaController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setValidator(validatorFactoryBean)
            .setHandlerExceptionResolvers()
            .build();

        setAuthenticationForRequest();
    }

    @Test
    public void getEula() throws Exception {
        final String request = requestRoot;

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        final String url = "example.org";

        given(eulaService.getEULA(Mockito.anyString()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(url));

        mockMvc.perform(getRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(url))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void putEula() throws Exception {
        final String request = requestRoot;

        log.info("Testing a PUT Request to %s%s\n".formatted(apiRoot, request));

        given(eulaService.putEULA(Mockito.anyString(), Mockito.any(EEULAStatus.class)))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(Messages.Error.EULA_ACTION_INVALID));

        mockMvc.perform(putRequest(request, ERole.ROLE_USER)
                .param("action", EEULAStatus.ACCEPT.name()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Messages.Error.EULA_ACTION_INVALID))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    private void setAuthenticationForRequest() throws ParseException {
        given(jwtUtils.getUserIdFromStringToken(Mockito.anyString())).willReturn(0);
        given(userRepository.existsById(Mockito.anyInt())).willReturn(Boolean.TRUE);
        given(userValidation.userFromTokenExistsInDB(Mockito.anyString())).willReturn(Boolean.TRUE);
    }
}
