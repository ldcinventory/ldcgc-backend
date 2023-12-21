package org.ldcgc.backend.service.users;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.nio.charset.StandardCharsets;

@Slf4j
@SpringBootTest
class VolunteerServiceImplTest {

    @MockBean VolunteerService volunteerService;

    private MockMvc mockMvc;
    private final PodamFactory factory = new PodamFactoryImpl();


    @BeforeEach
    public void init() {

        mockMvc = MockMvcBuilders
            .standaloneSetup(volunteerService)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setHandlerExceptionResolvers()
            .build();
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
