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

@Slf4j
@SpringBootTest
class UserServiceImplTest {

    @MockBean UserService userService;

    private MockMvc mockMvc;
    private UserDto mockedUser;
    private UserDto mockedUserLogin;

    @BeforeEach
    public void init() {

        mockMvc = MockMvcBuilders
            .standaloneSetup(userService)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setHandlerExceptionResolvers()
            .build();

    }

    // get my user
    @Test
    public void whenGetMyUser_returnUserNotFound() {

    }

    @Test
    public void whenGetMyUser_returnUser() {

    }

    // update my user
    @Test
    public void whenUpdateMy_returnUserUserNotFound() {

    }

    @Test
    public void whenUpdateMyUser_returnEmailAlreadyTaken() {

    }

    @Test
    public void whenUpdateMyUser_returnMyUserUpdated() {

    }

    // delete my user
    @Test
    public void whenDeleteMyUser_returnUserNotFound() {

    }

    @Test
    public void whenDeleteMyUser_returnOK() {

    }

    // create user
    @Test
    public void whenCreateUser_returnUserAlreadyTaken() {

    }

    @Test
    public void whenCreateUser_returnUserCreated() {

    }

    // get user
    @Test
    public void whenGetUser_returnUserNotFound() {

    }

    @Test
    public void whenGetUser_returnUser() {

    }

    // list users
    @Test
    public void whenListUsers_returnOneUser() {

    }

    @Test
    public void whenListUsers_returnMultipleUsers() {

    }

    // update user
    @Test
    public void whenUpdateUser_returnUserNotFound() {

    }

    @Test
    public void whenUpdateUser_returnEmailAlreadyTaken() {

    }

    @Test
    public void whenUpdateUser_returnUpdatedUser() {

    }

    // delete user
    @Test
    public void whenDeleteUser_returnUserNotFound() {

    }

    @Test
    public void whenDeleteUser_returnOK() {

    }

}
