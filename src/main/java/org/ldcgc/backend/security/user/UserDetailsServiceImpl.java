package org.ldcgc.backend.security.user;

import org.ldcgc.backend.exception.RequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static org.ldcgc.backend.util.retrieving.Messages.getErrorMessage;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin1234"))
                .roles("ADMIN")
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .build();

        UserDetails user = User.withUsername("user")
                .password(encoder.encode("user1234"))
                .roles("USER")
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();

        if(username.equals("admin"))
            return admin;
        if(username.equals("user"))
            return user;

        throw new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage("USER_NOT_FOUND"));
    }

}
