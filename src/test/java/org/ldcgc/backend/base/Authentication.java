package org.ldcgc.backend.base;

import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.validator.UserValidation;
import org.mockito.Mockito;

import java.text.ParseException;

import static org.mockito.BDDMockito.given;

public class Authentication {

    public static void setAuthenticationForRequest(JwtUtils jwtUtils, UserRepository userRepository, UserValidation userValidation) throws ParseException {
        given(jwtUtils.getUserIdFromStringToken(Mockito.anyString())).willReturn(0);
        given(userRepository.existsById(Mockito.anyInt())).willReturn(Boolean.TRUE);
        given(userValidation.userFromTokenExistsInDB(Mockito.anyString())).willReturn(Boolean.TRUE);
    }
}
