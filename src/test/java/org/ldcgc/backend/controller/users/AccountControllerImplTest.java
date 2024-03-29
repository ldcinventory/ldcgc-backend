package org.ldcgc.backend.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.base.mock.MockedToken;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.users.AccountService;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.creation.Email;
import org.ldcgc.backend.validator.UserValidation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.thymeleaf.TemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.ldcgc.backend.base.Constants.apiRoot;
import static org.ldcgc.backend.base.factory.TestRequestFactory.getRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.postRequest;
import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getRandomMockedUserDto;
import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getRandomMockedUserDtoLogin;
import static org.ldcgc.backend.util.conversion.Convert.localDateTimeToTimeStampString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerImplTest {
    // controller
    @Autowired private AccountController accountController;
    // services
    @MockBean private AccountService accountService;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtUtils jwtUtils;
    // repositories
    @MockBean private TokenRepository tokenRepository;
    @MockBean private UserRepository userRepository;
    // email
    @Autowired private TemplateEngine templateEngine;
    @MockBean private JavaMailSender sender;
    // other
    @MockBean private UserValidation userValidation;
    @MockBean private ContextConstants contextConstants;
    // mapper
    @Autowired private ObjectMapper mapper;

    private final String requestRoot = "/accounts";
    private final String JWT = "jwt";

    private MockMvc mockMvc;
    private UserDto mockedUser;
    private UserDto mockedUserLogin;

    @BeforeEach
    public void init() {

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

        given(accountService.login(Mockito.any(UserCredentialsDto.class))).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(mockedUser));

        mockMvc.perform(postRequest(request)
                .content(mapper.writeValueAsString(mockedUserLogin)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", CoreMatchers.is(mockedUser.getId())))
            .andExpect(jsonPath("$.email", CoreMatchers.is(mockedUser.getEmail())))
            .andExpect(jsonPath("$.role", CoreMatchers.containsStringIgnoringCase(mockedUser.getRole().getRoleName())))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void logoutUser() throws Exception {

        final String request = requestRoot + "/logout";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        given(jwtUtils.getUserIdFromStringToken(Mockito.anyString())).willReturn(0);
        given(userRepository.existsById(Mockito.anyInt())).willReturn(Boolean.TRUE);
        given(userValidation.userFromTokenExistsInDB(Mockito.anyString())).willReturn(Boolean.TRUE);

        given(accountService.logout(Mockito.anyString())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(Messages.Info.LOGOUT_SUCCESSFUL));

        mockMvc.perform(postRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Messages.Info.LOGOUT_SUCCESSFUL))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void recoverCredentials() throws Exception {
        final String request = requestRoot + "/recover";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        Email email = new Email(templateEngine, sender);
        Email.setINSTANCE(email);

        UserCredentialsDto credentialsDto = UserCredentialsDto.builder()
            .email(mockedUser.getEmail())
            .build();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        given(sender.createMimeMessage()).willReturn(mimeMessage);

        given(Email.sendRecoveringCredentials(mockedUser.getEmail(), JWT)).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.CREATED).body(Messages.Info.CREDENTIALS_EMAIL_SENT));

        given(accountService.recoverCredentials(any(UserCredentialsDto.class))).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.CREATED).body(Messages.Info.CREDENTIALS_EMAIL_SENT));

        mockMvc.perform(postRequest(request)
                .content(mapper.writeValueAsString(credentialsDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().string(Messages.Info.CREDENTIALS_EMAIL_SENT))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void validateTokenWhenRecoveringCredentials() throws Exception {
        final String request = requestRoot + "/validate";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        given(accountService.validateToken(JWT)).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(Messages.Info.RECOVERY_TOKEN_VALID));

        mockMvc.perform(getRequest(request)
                .param("recovery-token", JWT))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Messages.Info.RECOVERY_TOKEN_VALID))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void setNewPasswordWhenRecoveringCredentials() throws Exception {
        final String request = requestRoot + "/new-credentials";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        UserCredentialsDto credentialsDto = UserCredentialsDto.builder()
            .email(mockedUser.getEmail())
            .build();

        given(accountService.newCredentials(any(UserCredentialsDto.class))).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(Messages.Info.USER_CREDENTIALS_UPDATED));

        mockMvc.perform(postRequest(request)
                .content(mapper.writeValueAsString(credentialsDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Messages.Info.USER_CREDENTIALS_UPDATED))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void refreshToken() throws Exception {
        final String request = requestRoot + "/refresh-token";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        User user = UserMapper.MAPPER.toEntity(mockedUser);
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-refresh-token", MockedToken.generateRefreshToken(user).getParsedString());

        UserDto userDto = UserDto.builder()
            .tokenExpires(LocalDateTime.now().plusDays(1))
            .refreshExpires(LocalDateTime.now().plusMonths(1)).build();

        given(jwtUtils.getUserIdFromStringToken(Mockito.anyString())).willReturn(0);
        given(userRepository.existsById(Mockito.anyInt())).willReturn(Boolean.TRUE);
        given(userValidation.userFromTokenExistsInDB(Mockito.anyString())).willReturn(Boolean.TRUE);
        given(accountService.refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class), anyString()))
            .willAnswer(invocation -> Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.TOKEN_REFRESHED, userDto));

        String tokenExpires = localDateTimeToTimeStampString(userDto.getTokenExpires());
        String refreshExpires = localDateTimeToTimeStampString(userDto.getRefreshExpires());

        mockMvc.perform(postRequest(request, ERole.ROLE_USER)
                .headers(headers))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message", Matchers.is(Messages.Info.TOKEN_REFRESHED)))
            .andExpect(jsonPath("$.data.tokenExpires", Matchers.is(tokenExpires)))
            .andExpect(jsonPath("$.data.refreshExpires", Matchers.is(refreshExpires)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

}
