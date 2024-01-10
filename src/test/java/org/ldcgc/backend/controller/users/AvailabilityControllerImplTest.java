package org.ldcgc.backend.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.mock.MockedAbsencesAvailability;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.db.repository.users.AbsenceRepository;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.users.AvailabilityService;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.common.EWeekday;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collections;
import java.util.Set;

import static org.ldcgc.backend.base.Authentication.setAuthenticationForRequest;
import static org.ldcgc.backend.base.Constants.apiRoot;
import static org.ldcgc.backend.base.factory.TestRequestFactory.deleteRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.getRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.putRequest;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = AvailabilityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AvailabilityControllerImplTest {

    // controller
    @Autowired private AvailabilityController availabilityController;

    // services
    @MockBean private AvailabilityService availabilityService;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtUtils jwtUtils;

    // repositories
    @MockBean private TokenRepository tokenRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private AbsenceRepository absenceRepository;
    @MockBean private VolunteerRepository volunteerRepository;

    // other
    @MockBean private UserValidation userValidation;
    @MockBean private ContextConstants contextConstants;

    // context
    @Autowired private WebApplicationContext context;

    // mapper
    @Autowired private ObjectMapper mapper;

    private final String requestRoot = "/availability";

    private MockMvc mockMvc;
    private Set<EWeekday> mockedAvailability;

    @BeforeEach
    public void init() throws ParseException {

        final GenericWebApplicationContext context = new GenericWebApplicationContext(new MockServletContext());
        final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
        beanFactory.registerSingleton(UserValidation.class.getCanonicalName(), UserValidation.bean(userRepository, jwtUtils));
        context.refresh();

        mockMvc = MockMvcBuilders
            .standaloneSetup(availabilityController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setHandlerExceptionResolvers()
            .build();

        mockedAvailability = MockedAbsencesAvailability.getRandomAvailability();

        setAuthenticationForRequest(jwtUtils, userRepository, userValidation);

    }

    // me
    @Test
    public void getMyAvailability() throws Exception {

        final String request = requestRoot + "/me";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        given(availabilityService.getMyAvailability(Mockito.anyString())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(mockedAvailability)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(mockedAvailability)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void updateMyAvailability() throws Exception {

        final String request = requestRoot + "/me";

        log.info("Testing a PUT Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.AVAILABILITY_UPDATED).data(mockedAvailability).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(availabilityService.updateMyAvailability(Mockito.anyString(), Mockito.any()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(putRequest(request, ERole.ROLE_ADMIN)
                .content(mapper.writeValueAsString(mockedAvailability)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void clearMyAvailability() throws Exception {

        final String request = requestRoot + "/me";

        log.info("Testing a DELETE Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.AVAILABILITY_CLEARED).data(Collections.emptyList()).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(availabilityService.clearMyAvailability(Mockito.anyString()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(response));

        mockMvc.perform(deleteRequest(request, ERole.ROLE_USER, "0"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    // managed

    @Test
    public void getAvailability() throws Exception {

        final String request = requestRoot + "/12345";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        given(availabilityService.getAvailability(Mockito.anyString())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(mockedAvailability)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_ADMIN))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(mockedAvailability)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void updateAvailability() throws Exception {

        final String request = requestRoot + "/12345";

        log.info("Testing a PUT Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.AVAILABILITY_UPDATED).data(mockedAvailability).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(availabilityService.updateAvailability(Mockito.anyString(), Mockito.any()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(putRequest(request, ERole.ROLE_ADMIN)
                .content(mapper.writeValueAsString(mockedAvailability)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void clearAvailability() throws Exception {

        final String request = requestRoot + "/12345";

        log.info("Testing a DELETE Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.AVAILABILITY_CLEARED).data(Collections.emptyList()).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(availabilityService.clearAvailability(Mockito.anyString()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(response));

        mockMvc.perform(deleteRequest(request, ERole.ROLE_ADMIN, "0"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

}
