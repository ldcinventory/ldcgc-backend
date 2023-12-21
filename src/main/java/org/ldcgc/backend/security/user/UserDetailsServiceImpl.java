package org.ldcgc.backend.security.user;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {

        org.ldcgc.backend.db.model.users.User user = userRepository.findByEmail(email).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

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

}
