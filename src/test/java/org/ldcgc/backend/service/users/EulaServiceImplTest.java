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
class EulaServiceImplTest {

    @MockBean EulaService eulaService;

    private MockMvc mockMvc;
    private UserDto mockedUser;
    private UserDto mockedUserLogin;

    @BeforeEach
    public void init() {

        mockMvc = MockMvcBuilders
            .standaloneSetup(eulaService)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setHandlerExceptionResolvers()
            .build();

        mockedUser = getRandomMockedUserDto();
        mockedUserLogin = getRandomMockedUserDtoLogin();

    }

    // getEula

    @Test
    public void whenGetEula_returnUserNotFound() {

    }

    @Test
    public void whenGetEula_returnStandardEula() {

    }

    @Test
    public void whenGetEula_returnManagerEula() {

    }

    @Test
    public void whenGetEula_returnEulaAlreadyAccepted() {

    }

    // putEula

    @Test
    public void whenUpdateEula_returnUserNotFound() {

    }

    @Test
    public void whenUpdateEulaAndUserIsStandard_returnEulaAlreadyAccepted() {

    }

    @Test
    public void whenUpdateEulaAndUserIsManagerOrAdmin_returnEulaAlreadyAccepted() {

    }

    @Test
    public void whenAcceptStandardEula_returnStandardEulaAccepted() {

    }

    @Test
    public void whenAcceptManagerEula_returnManagerEulaAccepted() {

    }

    @Test
    public void whenPendingEula_returnStandardEulaPending() {

    }

    @Test
    public void whenPendingEula_returnManagerEulaPending() {

    }

    @Test
    public void whenRejectStandardEula_returnUserDeleted() {

    }

    @Test
    public void whenRejectManagerEula_returnUserDowngraded() {

    }

    @Test
    public void whenUpdateEula_returnActionInvalid() {

    }

}
