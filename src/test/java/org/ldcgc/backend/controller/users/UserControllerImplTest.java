package org.ldcgc.backend.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.base.mock.MockedUserDetails;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.users.UserService;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import static org.ldcgc.backend.base.Constants.apiRoot;
import static org.ldcgc.backend.base.factory.TestRequestFactory.deleteRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.getRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.postRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.putRequest;
import static org.ldcgc.backend.base.mock.MockedUserDetails.getListOfMockedUsers;
import static org.ldcgc.backend.base.mock.MockedUserDetails.getRandomMockedUserDto;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerImplTest {

    // controller
    @Autowired private UserController userController;

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
    private UserDto mockedUser;

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
            .standaloneSetup(userController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setValidator(validatorFactoryBean)
            .setHandlerExceptionResolvers()
            .build();

        mockedUser = getRandomMockedUserDto();

        setAuthenticationForRequest();

    }

    // my user
    @Test
    public void getMyUser() throws Exception {

        final String request = requestRoot + "/me";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        given(userService.getMyUser(Mockito.anyString())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(mockedUser)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(mockedUser)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void updateMyUser() throws Exception {
        final String request = requestRoot + "/me";

        log.info("Testing a PUT Request to %s%s\n".formatted(apiRoot, request));

        UserDto mockedUser = MockedUserDetails.getRandomMockedUpdatingUserDto(ERole.ROLE_USER);
        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.USER_UPDATED).data(mockedUser).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(userService.updateMyUser(Mockito.anyString(), Mockito.any(UserDto.class))).will(
            invocation -> ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(putRequest(request, ERole.ROLE_USER)
                .content(mapper.writeValueAsString(mockedUser)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void deleteMyUser() throws Exception {
        final String request = requestRoot + "/me";

        log.info("Testing a DELETE Request to %s%s\n".formatted(apiRoot, request));

        given(userService.deleteMyUser(Mockito.anyString()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(Messages.Info.USER_DELETED));

        mockMvc.perform(deleteRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Messages.Info.USER_DELETED))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    // any user

    @Test
    public void createUser() throws Exception {
        final String request = requestRoot;

        log.info("Testing a PUT Request to %s%s\n".formatted(apiRoot, request));

        UserDto mockedUser = getRandomMockedUserDto(ERole.ROLE_ADMIN);
        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.USER_CREATED).data(mockedUser).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(userService.createUser(Mockito.anyString(), Mockito.any(UserDto.class)))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(postRequest(request, ERole.ROLE_ADMIN)
                .content(mapper.writeValueAsString(mockedUser)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void getUser() throws Exception {
        final String request = requestRoot + "/{userId}";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        given(userService.getUser(Mockito.anyInt()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(mockedUser)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER, "0"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(mockedUser)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void listUsers() throws Exception {
        final String request = requestRoot;

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        var users = getListOfMockedUsers(5);

        final String message = String.format(Messages.Info.USER_LISTED, 5);
        Response.DTO responseDTO = Response.DTO.builder().message(message).data(users).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(userService.listUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.isNull()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(response));

        mockMvc.perform(getRequest(request, ERole.ROLE_USER)
                .param("pageIndex", "0")
                .param("size", "5")
                .param("filterString", "ad")
                .param("userId", "")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void updateUser() throws Exception {
        final String request = requestRoot + "/{userId}";

        log.info("Testing a PUT Request to %s%s\n".formatted(apiRoot, request));

        UserDto mockedUser = MockedUserDetails.getRandomMockedUpdatingUserDto(ERole.ROLE_ADMIN);
        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.USER_UPDATED).data(mockedUser).build();

        given(userService.updateUser(Mockito.anyString(), Mockito.anyInt(), Mockito.any(UserDto.class))).will(
            invocation -> ResponseEntity.status(HttpStatus.CREATED).body(responseDTO));

        mockMvc.perform(putRequest(request, ERole.ROLE_ADMIN, "0")
                .content(mapper.writeValueAsString(mockedUser)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(responseDTO)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void deleteUser() throws Exception {
        final String request = requestRoot + "/{userId}";

        log.info("Testing a DELETE Request to %s%s\n".formatted(apiRoot, request));

        given(userService.deleteUser(Mockito.anyInt()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(Messages.Info.USER_DELETED));

        mockMvc.perform(deleteRequest(request, ERole.ROLE_USER, "0"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Messages.Info.USER_DELETED))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    private void setAuthenticationForRequest() throws ParseException {
        given(jwtUtils.getUserIdFromStringToken(Mockito.anyString())).willReturn(0);
        given(userRepository.existsById(Mockito.anyInt())).willReturn(Boolean.TRUE);
        given(userValidation.userFromTokenExistsInDB(Mockito.anyString())).willReturn(Boolean.TRUE);
    }

}
