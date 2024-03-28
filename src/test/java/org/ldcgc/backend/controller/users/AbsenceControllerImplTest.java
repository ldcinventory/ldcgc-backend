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
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.users.AbsenceService;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.constants.Messages;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import static org.ldcgc.backend.base.Authentication.setAuthenticationForRequest;
import static org.ldcgc.backend.base.Constants.apiRoot;
import static org.ldcgc.backend.base.factory.TestRequestFactory.deleteRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.getRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.postRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.putRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = AbsenceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AbsenceControllerImplTest {

    // controller
    @Autowired private AbsenceController absenceController;

    // services
    @MockBean private AbsenceService absenceService;
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

    private final String requestRoot = "/absences";

    private MockMvc mockMvc;
    private AbsenceDto mockedAbsence;
    private List<AbsenceDto> mockedAbsences;

    @BeforeEach
    public void init() throws ParseException {

        final GenericWebApplicationContext context = new GenericWebApplicationContext(new MockServletContext());
        final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
        beanFactory.registerSingleton(UserValidation.class.getCanonicalName(), UserValidation.bean(userRepository, jwtUtils));
        context.refresh();

        mockMvc = MockMvcBuilders
            .standaloneSetup(absenceController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setHandlerExceptionResolvers()
            .build();

        mockedAbsence = MockedAbsencesAvailability.getRandomAbsences(1).getFirst();
        mockedAbsences = MockedAbsencesAvailability.getRandomAbsences(5);

        setAuthenticationForRequest(jwtUtils, userRepository, userValidation);

    }

    // my user
    @Test
    public void getMyAbsence() throws Exception {

        final String request = requestRoot + "/me/12345";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        given(absenceService.getMyAbsence(anyString(), anyInt())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(mockedAbsence)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(mockedAbsence)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void listMyAbsencesUnfiltered() throws Exception {

        final String request = requestRoot + "/me";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        final String message = String.format(Messages.Info.ABSENCES_LISTED, 5);
        Response.DTO responseDTO = Response.DTO.builder().message(message).data(mockedAbsences).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(absenceService.listMyAbsences(anyString(), anyInt(), anyInt(), isNull(), isNull(), anyString())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(response)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void listMyAbsencesFiltered() throws Exception {

        final String request = requestRoot + "/me";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        final String message = String.format(Messages.Info.ABSENCES_LISTED, 5);
        Response.DTO responseDTO = Response.DTO.builder().message(message).data(mockedAbsences).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(absenceService.listMyAbsences(anyString(), anyInt(), anyInt(), any(LocalDate.class), any(LocalDate.class), anyString())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(response)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER)
                .param("dateFrom", "2024-09-30")
                .param("dateTo", "2024-01-01")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }
    @Test
    public void createMyAbsence() throws Exception {

        final String request = requestRoot + "/me";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.ABSENCE_CREATED).data(mockedAbsence).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(absenceService.createMyAbsence(anyString(), any(AbsenceDto.class)))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(postRequest(request, ERole.ROLE_ADMIN)
                .content(mapper.writeValueAsString(mockedAbsence)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void updateMyAbsence() throws Exception {

        final String request = requestRoot + "/me/12345";

        log.info("Testing a PUT Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.ABSENCE_UPDATED).data(mockedAbsence).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(absenceService.updateMyAbsence(anyString(), anyInt(), any(AbsenceDto.class)))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(putRequest(request, ERole.ROLE_ADMIN)
                .content(mapper.writeValueAsString(mockedAbsence)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void deleteMyAbsence() throws Exception {

        final String request = requestRoot + "/me/12345";

        log.info("Testing a DELETE Request to %s%s\n".formatted(apiRoot, request));

        given(absenceService.deleteMyAbsence(anyString(), anyInt()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(Messages.Info.ABSENCE_DELETED));

        mockMvc.perform(deleteRequest(request, ERole.ROLE_USER, "0"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Messages.Info.ABSENCE_DELETED))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    // managed
    @Test
    public void getAbsence() throws Exception {
        final String request = requestRoot + "/12345";

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        given(absenceService.getAbsence(anyInt())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(mockedAbsence)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_MANAGER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(mockedAbsence)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void listAbsencesUnfiltered() throws Exception {

        final String request = requestRoot;

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        final String message = String.format(Messages.Info.ABSENCES_LISTED, 5);
        Response.DTO responseDTO = Response.DTO.builder().message(message).data(mockedAbsences).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(absenceService.listAbsences(anyInt(), anyInt(), isNull(), isNull(), isNull(), anyString(), anyBoolean())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(response)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void listAbsencesFilteredByDate() throws Exception {

        final String request = requestRoot;

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        final String message = String.format(Messages.Info.ABSENCES_LISTED, 5);
        Response.DTO responseDTO = Response.DTO.builder().message(message).data(mockedAbsences).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(absenceService.listAbsences(anyInt(), anyInt(), any(LocalDate.class), any(LocalDate.class), isNull(), anyString(), anyBoolean())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(response)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER)
                .param("dateFrom", "2024-01-01")
                .param("dateTo", "2024-09-30")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void listAbsencesFilteredByVolunteers() throws Exception {
        final String request = requestRoot;

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        final String message = String.format(Messages.Info.ABSENCES_LISTED, 5);
        Response.DTO responseDTO = Response.DTO.builder().message(message).data(mockedAbsences).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(absenceService.listAbsences(anyInt(), anyInt(), isNull(), isNull(), anyList(), anyString(), anyBoolean())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(response)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER)
                .param("builderAssistantIds", "12345","67890")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void listAbsencesFilteredByDateAndVolunteers() throws Exception {

        final String request = requestRoot;

        log.info("Testing a GET Request to %s%s\n".formatted(apiRoot, request));

        final String message = String.format(Messages.Info.ABSENCES_LISTED, 5);
        Response.DTO responseDTO = Response.DTO.builder().message(message).data(mockedAbsences).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        given(absenceService.listAbsences(anyInt(), anyInt(), any(LocalDate.class), any(LocalDate.class), anyList(), anyString(), anyBoolean())).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.OK).body(response)
        );

        mockMvc.perform(getRequest(request, ERole.ROLE_USER)
                .param("dateFrom", "2024-01-01")
                .param("dateTo", "2024-09-30")
                .param("builderAssistantIds", "12345","67890")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void createAbsence() throws Exception {

        final String request = requestRoot;

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.ABSENCE_CREATED).data(mockedAbsence).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(absenceService.createAbsence(any(AbsenceDto.class)))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(postRequest(request, ERole.ROLE_MANAGER)
                .content(mapper.writeValueAsString(mockedAbsence)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    public void updateAbsence() throws Exception {

        final String request = requestRoot + "/12345";

        log.info("Testing a POST Request to %s%s\n".formatted(apiRoot, request));

        Response.DTO responseDTO = Response.DTO.builder().message(Messages.Info.ABSENCE_UPDATED).data(mockedAbsence).build();
        ResponseEntity<Response.DTO> response = ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        given(absenceService.updateAbsence(anyInt(), any(AbsenceDto.class)))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(putRequest(request, ERole.ROLE_MANAGER)
                .content(mapper.writeValueAsString(mockedAbsence)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(response)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    public void deleteAbsence() throws Exception {

        final String request = requestRoot + "/12345";

        log.info("Testing a DELETE Request to %s%s\n".formatted(apiRoot, request));

        given(absenceService.deleteAbsence(anyInt()))
            .willAnswer(invocation -> ResponseEntity.status(HttpStatus.OK).body(Messages.Info.ABSENCE_DELETED));

        mockMvc.perform(deleteRequest(request, ERole.ROLE_MANAGER, "0"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Messages.Info.ABSENCE_DELETED))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

}
