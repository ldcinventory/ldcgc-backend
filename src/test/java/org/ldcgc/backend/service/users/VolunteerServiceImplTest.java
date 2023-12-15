package org.ldcgc.backend.service.users;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.ldcgc.backend.base.mock.MockedUserDetails.getRandomMockedUserDto;
import static org.ldcgc.backend.base.mock.MockedUserDetails.getRandomMockedUserDtoLogin;

@Slf4j
@SpringBootTest
class VolunteerServiceImplTest {

    @MockBean VolunteerService volunteerService;

    private MockMvc mockMvc;
    private UserDto mockedUser;
    private UserDto mockedUserLogin;

    @BeforeEach
    public void init() {

        mockMvc = MockMvcBuilders
            .standaloneSetup(volunteerService)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setHandlerExceptionResolvers()
            .build();

        mockedUser = getRandomMockedUserDto();
        mockedUserLogin = getRandomMockedUserDtoLogin();

    }

    //create vounteer
    @Test
    public void whenCreateVolunteer_returnVolunteerAlreadyExists() {

    }

    @Test
    public void whenCreateVolunteer_returnVolunteerCreated() {

    }

    //list volunteer
    @Test
    public void whenListVolunteerAndSearchByBuilderAssistantId_returnOneVolunteer() {

    }

    @Test
    public void whenListVolunteer_returnOneVolunteer() {

    }

    @Test
    public void whenListVolunteer_returnMultipleVolunteers() {

    }

    //get my volunteer
    @Test
    public void whenGetMyVolunteer_returnVolunteerNotFound() {

    }

    @Test
    public void whenGetMyVolunteer_returnMyVolunteer() {

    }

    //get volunteer
    @Test
    public void whenGetVolunteer_returnVolunteerNotFound() {

    }

    @Test
    public void whenGetVolunteer_returnMyVolunteer() {

    }

    //update volunteer
    @Test
    public void whenUpdateVolunteer_returnVolunteerNotFound() {

    }

    @Test
    public void whenUpdateVolunteer_returnVolunteerBuilderAssistanIdTaken() {

    }

    @Test
    public void whenUpdateVolunteer_returnVolunteerUpdated() {

    }

    //delete volunteer
    @Test
    public void whenDeleteVolunteer_returnVolunteerNotFound() {

    }

    @Test
    public void whenDeleteVolunteer_returnVolunteerDeleted() {

    }

}
