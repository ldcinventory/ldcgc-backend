package org.ldcgc.backend.security.user;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.USER_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // TODO delete these 2 parameters in the future to force login
    private static String useMockedUser;
    private static boolean skipLogin;

    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {

        // TODO delete this section in the future to force login
        if(skipLogin) {

            // set to false to avoid skipLogin in next calls by accident
            skipLogin = false;

            if (useMockedUser.equals("admin")) {
                UserDetails userDetails = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles("ADMIN")
                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    .build();
                return new UserDetailsImpl(userDetails, 1, null, null);
            }

            if (useMockedUser.equals("manager")) {
                UserDetails userDetails = User.builder()
                    .username("manager")
                    .password(passwordEncoder.encode("manager"))
                    .roles("MANAGER")
                    .authorities(new SimpleGrantedAuthority("ROLE_MANAGER"))
                    .build();
                return new UserDetailsImpl(userDetails, 1, null, null);
            }

            if (useMockedUser.equals("standard")) {
                UserDetails userDetails = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user"))
                    .roles("USER")
                    .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                    .build();
                return new UserDetailsImpl(userDetails, 1, null, null);
            }
        }

        org.ldcgc.backend.db.model.users.User user = userRepository.findByEmail(email).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND)));

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

        UserDetails userDetails = User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().getRoleName().toUpperCase())
            .authorities(authorities)
            .build();

        return new UserDetailsImpl(userDetails, user.getId(), user.getAcceptedEULA(), user.getAcceptedEULAManager());

    }

    // TODO delete this section in the future to force login
    public static void setMockedUser(String mockedUser) {
        UserDetailsServiceImpl.useMockedUser = mockedUser;
        UserDetailsServiceImpl.skipLogin = true;
    }

}
