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
class AccountServiceImplTest {

    @MockBean private AccountService accountService;

    private MockMvc mockMvc;
    private UserDto mockedUser;
    private UserDto mockedUserLogin;

    @BeforeEach
    public void init() {

        mockMvc = MockMvcBuilders
            .standaloneSetup(accountService)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setHandlerExceptionResolvers()
            .build();

        mockedUser = getRandomMockedUserDto();
        mockedUserLogin = getRandomMockedUserDtoLogin();

    }

    // -> login
    @Test
    public void whenAuthenticateUser_returnUserDetails() {

    }

    @Test
    public void whenAuthenticateUser_returnEmailIsInvalid() {

    }

    @Test
    public void whenAuthenticateUser_returnPasswordIsInvalid() {

    }

    // -> logout
    @Test
    public void whenLogoutUser_returnOK() {

    }

    // -> recover
    @Test
    public void whenRecoverCredentials_returnEmailSent() {

    }

    @Test
    public void whenRecoverCredentials_returnUserNotFound() {

    }

    // -> validate token
    @Test
    public void whenValidatingTokenRecoveringCredentials_returnTokenValidated() {

    }

    @Test
    public void whenValidatingTokenRecoveringCredentials_returnTokenNotExist() {

    }

    @Test
    public void whenValidatingTokenRecoveringCredentials_returnTokenNotForRecovery() {

    }

    @Test
    public void whenValidatingTokenRecoveringCredentials_returnUserNotFound() {

    }

    // -> new credentials
    @Test
    public void whenSettingNewCredentials_returnCredentialsUpdated() {

    }

    @Test
    public void whenSettingNewCredentials_returnUserNotFound() {

    }

}
