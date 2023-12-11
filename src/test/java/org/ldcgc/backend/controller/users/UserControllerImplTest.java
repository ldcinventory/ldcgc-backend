package org.ldcgc.backend.controller.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.service.users.AccountService;
import org.ldcgc.backend.service.users.UserService;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.LOGOUT_SUCCESSFUL;
import static org.ldcgc.backend.util.retrieving.Message.getInfoMessage;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerImplTest {

    @MockBean private UserService userService;
    @MockBean private AccountService accountService;

    @Autowired private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .addFilter((request, response, chain) -> {
                response.setCharacterEncoding("UTF-8");
                chain.doFilter(request, response);
            }, "/*")
            .apply(springSecurity())
            .build();
    }

    @BeforeEach
    public void init() {

    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    public void authenticateUserWithValidCredentials() throws Exception {

        assertThat(mockMvc.perform(
            post("/accounts/login")
                .characterEncoding("utf-8")
                .content("""
                    {
                      "email": "user@user",
                      "password": "user"
                    }
                    """)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().encoding(StandardCharsets.UTF_8))
            .andExpect(content().string(containsString(getInfoMessage(LOGOUT_SUCCESSFUL)))));
    }

    @Test
    public void authenticateUserWithWrongCredentials() {

    }

    @Test
    public void authenticateWithoutCredentials() {

    }

}
