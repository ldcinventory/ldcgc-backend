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
    // service
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
    public void authenticateUser() {

    }

    @Test
    public void authenticateUserEmailIsInvalid() {

    }

    @Test
    public void authenticateUserPasswordIsInvalid() {

    }

    // -> logout
    @Test
    public void logoutUser() {

    }

    // -> recover
    @Test
    public void recoverCredentials() {

    }

    @Test
    public void recoverCredentialsUserNotFound() {

    }

    // -> validate token
    @Test
    public void validateTokenWhenRecoveringCredentials() {

    }

    @Test
    public void validateTokenWhenRecoveringCredentialsTokenNotExists() {

    }

    @Test
    public void validateTokenWhenRecoveringCredentialsTokenNotForRecovery() {

    }

    @Test
    public void validateTokenWhenRecoveringCredentialsUserNotFound() {

    }

    // -> new credentials
    @Test
    public void setNewPasswordWhenRecoveringCredentials() {

    }

    @Test
    public void setNewPasswordWhenRecoveringCredentialsUserNotFound() {

    }

}
