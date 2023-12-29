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

    private final UserDto notFoundUser = UserDto.builder().build();

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
    public void whenUpdateUser_validateUserAlreadyExists() {

    }

    @Test
    public void whenUpdateUser_validateChangeSelfRole() {

    }

    @Test
    public void whenUpdateUser_validateManagerCreatingAdmin() {

    }

    @Test
    public void whenUpdateUser_validateManagerElevatingToAdmin() {

    }

    @Test
    public void whenUpdateUserWithVolunteer_returnVolunteerNotFound() {

    }

    @Test
    public void whenUpdateUserWithVolunteer_returnVolunteerAssigned() {

    }

    @Test
    public void whenUpdateUserWithResponsibility_returnCategoryNotFound() {

    }

    @Test
    public void whenUpdateUserWithGroup_returnGroupNotFound() {

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
