package org.ldcgc.backend.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.users.AccountService;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.validator.UserValidation;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.ldcgc.backend.base.Constants.apiRoot;
import static org.ldcgc.backend.base.factory.TestRequestFactory.postRequest;
import static org.ldcgc.backend.base.mock.MockedUserDetails.getRandomMockedUserDto;
import static org.ldcgc.backend.base.mock.MockedUserDetails.getRandomMockedUserDtoLogin;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.LOGOUT_SUCCESSFUL;
import static org.ldcgc.backend.util.retrieving.Message.getInfoMessage;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AccountControllerImplTest {
    // controller
    @Autowired private AccountController accountController;
    // services
    @MockBean private AccountService accountService;
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

    private final String requestRoot = "/accounts";

    private MockMvc mockMvc;
    private UserDto mockedUser;
    private UserDto mockedUserLogin;
    private TestConstrainValidationFactory constrainValidationFactory;

    @BeforeEach
    public void init() {

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
            .standaloneSetup(accountController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setValidator(validatorFactoryBean)
            .setHandlerExceptionResolvers()
            .build();

        mockedUser = getRandomMockedUserDto();
        mockedUserLogin = getRandomMockedUserDtoLogin();

    }

    @Test
    public void authenticateUser() throws Exception {

        final String request = requestRoot + "/login";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        given(accountService.login(ArgumentMatchers.any())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(mockedUser));

        mockMvc.perform(postRequest(request)
                .content(mapper.writeValueAsString(mockedUserLogin)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(mockedUser.getId())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(mockedUser.getEmail())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role", CoreMatchers.containsStringIgnoringCase(mockedUser.getRole().getRoleName())))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    //@WithMockUser(username="admin",roles={"USER","MANAGER","ADMIN"})
    public void logoutUser() throws Exception {

        final String request = requestRoot + "/logout";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        given(jwtUtils.getUserIdFromStringToken(Mockito.anyString())).willReturn(0);

        given(userRepository.existsById(Mockito.anyInt())).willReturn(Boolean.TRUE);

        given(userValidation.userFromTokenExistsInDB(Mockito.anyString())).willReturn(Boolean.TRUE);

        given(accountService.logout(ArgumentMatchers.any())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(getInfoMessage(LOGOUT_SUCCESSFUL)));

        mockMvc.perform(postRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(getInfoMessage(LOGOUT_SUCCESSFUL)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void recoverCredentials() {
        final String request = requestRoot + "/recover";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));
    }

    @Test
    public void validateTokenWhenRecoveringCredentials() {
        final String request = requestRoot + "/validate";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));
    }

    @Test
    public void setNewPasswordWhenRecoveringCredentials() {
        final String request = requestRoot + "/new-credentials";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));
    }

}
