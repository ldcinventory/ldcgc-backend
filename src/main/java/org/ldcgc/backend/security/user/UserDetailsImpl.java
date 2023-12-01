package org.ldcgc.backend.security.user;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

public class UserDetailsImpl extends User implements UserDetails {

    @Getter
    private final Integer id;

    @Getter
    private final LocalDateTime acceptedEULA;

    @Getter
    private final LocalDateTime acceptedEULAManager;

    public UserDetailsImpl(UserDetails userDetails, Integer id,
                           LocalDateTime acceptedEULA,
                           LocalDateTime acceptedEULAManager) {
        super(userDetails.getUsername(), userDetails.getPassword(), userDetails.isEnabled(), userDetails.isAccountNonExpired(), userDetails.isCredentialsNonExpired(), userDetails.isAccountNonExpired(), userDetails.getAuthorities());
        this.id = id;
        this.acceptedEULA = acceptedEULA;
        this.acceptedEULAManager = acceptedEULAManager;
    }

    public boolean equals(Object o) {
        return o instanceof UserDetailsImpl && this.id.equals(((UserDetailsImpl) o).getId());
    }

}
