package org.ldcgc.backend.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.mock.MockedUserVolunteer;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTests {

    private UserDetailsServiceImpl userDetailsService;
    @Mock private UserRepository userRepository;

    private User user;

    @BeforeEach
    void init() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
        user = MockedUserVolunteer.getRandomMockedUser();
    }

    @Test
    public void loadUserByUsername() {
        doReturn(Optional.of(user)).when(userRepository).findByEmail(Mockito.anyString());
        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername("test@test.com");

        Optional<GrantedAuthority> role = userDetails.getAuthorities().stream().findFirst();
        assertFalse(role.isEmpty());
        assertEquals(user.getRole().name(), role.get().getAuthority());

        verify(userRepository, atMostOnce()).findByEmail(user.getEmail());

    }

    @Test
    public void loadUserByUsernameUserNotFound() throws RequestException {
        doReturn(Optional.empty()).when(userRepository).findByEmail(Mockito.anyString());
        RequestException ex = assertThrows(RequestException.class, () -> userDetailsService.loadUserByUsername("test@test.com"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findByEmail(user.getEmail());

    }
}
