package org.ldcgc.backend.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.base.mock.MockedUserVolunteer;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.users.VolunteerService;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.validator.UserValidation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import static org.ldcgc.backend.base.Authentication.setAuthenticationForRequest;
import static org.ldcgc.backend.base.Constants.apiRoot;
import static org.ldcgc.backend.base.factory.TestRequestFactory.deleteRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.getRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.postMultipartRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.postRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.putRequest;
import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getListOfMockedUsers;
import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getRandomMockedUserDto;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = VolunteerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VolunteerControllerImplTest {

    // controller
    @Autowired private VolunteerController volunteerController;
    // services
    @MockBean private VolunteerService volunteerService;
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

    private final String requestRoot = "/volunteers";

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
        TestConstrainValidationFactory constrainValidationFactory = new TestConstrainValidationFactory(context);
        validatorFactoryBean.setConstraintValidatorFactory(constrainValidationFactory);
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders
            .standaloneSetup(volunteerController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setValidator(validatorFactoryBean)
            .setHandlerExceptionResolvers()
            .build();

        mockedUser = getRandomMockedUserDto();

        setAuthenticationForRequest(jwtUtils, userRepository, userValidation);

    }

    @Test
    public void getMyVolunteer() throws Exception {
        final String request = requestRoot + "/me";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.USER_UPDATED).data(mockedUser.getVolunteer()).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(volunteerService.getMyVolunteer(Mockito.anyString())).will(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(response));

        mockMvc.perform(getRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void getVolunteer() throws Exception {
        final String request = requestRoot + "/{volunteerId}";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.USER_UPDATED).data(mockedUser.getVolunteer()).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(volunteerService.getVolunteer(Mockito.anyString())).will(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(response));

        mockMvc.perform(getRequest(request, ERole.ROLE_MANAGER, "0"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void createVolunteer() throws Exception {
        final String request = requestRoot;

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        VolunteerDto mockedVolunteer = MockedUserVolunteer.getRandomMockedUserDto(ERole.ROLE_ADMIN).getVolunteer();
        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.VOLUNTEER_CREATED).data(mockedVolunteer).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(volunteerService.createVolunteer(Mockito.any(VolunteerDto.class)))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(postRequest(request, ERole.ROLE_ADMIN)
                .content(mapper.writeValueAsString(mockedUser)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void listVolunteers() throws Exception {
        final String request = requestRoot;

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        var volunteers = getListOfMockedUsers(5).stream().map(UserDto::getVolunteer).toList();

        final String message = String.format(Messages.Info.VOLUNTEER_LISTED, 5);
        Response.DTO responseDTO = Response.DTO.builder().message(message).data(volunteers).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(volunteerService.listVolunteers(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.isNull(), Mockito.anyString()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(response));

        mockMvc.perform(getRequest(request, ERole.ROLE_MANAGER)
                .param("pageIndex", "0")
                .param("size", "5")
                .param("filterString", "ad")
                .param("volunteerId", "")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void updateVolunteer() throws Exception {
        final String request = requestRoot + "/{volunteerId}";

        log.info("Testing a PUT Request to %s%s\n".formatted(apiRoot, request));

        UserDto mockedUser = MockedUserVolunteer.getRandomMockedUpdatingUserDto(ERole.ROLE_ADMIN);
        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.USER_UPDATED).data(mockedUser).build();

        given(volunteerService.updateVolunteer(Mockito.anyString(), Mockito.any(VolunteerDto.class))).will(
            invocation -> ResponseEntity.status(HttpStatus.CREATED).body(responseDTO));

        mockMvc.perform(putRequest(request, ERole.ROLE_ADMIN, "0")
                .content(mapper.writeValueAsString(mockedUser)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(responseDTO)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void deleteVolunteer() throws Exception {
        final String request = requestRoot + "/{volunteerId}";

        log.info("Testing a DELETE Request to %s%s\n".formatted(apiRoot, request));

        given(volunteerService.deleteVolunteer(Mockito.anyString()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(Messages.Info.USER_DELETED));

        mockMvc.perform(deleteRequest(request, ERole.ROLE_ADMIN, "0"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Messages.Info.USER_DELETED))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void uploadVolunteers() throws Exception {
        final String request = requestRoot + "/upload";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        given(volunteerService.uploadVolunteers(Mockito.anyInt(), Mockito.any(MultipartFile.class)))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.CREATED).body(String.format(Messages.Info.CSV_VOLUNTEERS_CREATED, 10)));

        MockMultipartFile file = new MockMultipartFile("document", "volunteers.csv", "text/csv", "50280100,Daniel,Albert,true,,x,,x,x,,,x".getBytes());

        mockMvc.perform(postMultipartRequest(request, file, ERole.ROLE_ADMIN)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("groupId", "8"))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().string(String.format(Messages.Info.CSV_VOLUNTEERS_CREATED, 10)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

}
