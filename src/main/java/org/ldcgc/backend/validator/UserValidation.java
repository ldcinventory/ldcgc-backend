package org.ldcgc.backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class UserValidation implements ConstraintValidator<UserFromTokenInDb, String> {

    public static final UserValidation instance = new UserValidation();

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtils jwtUtils;

    @Bean
    public static UserValidation bean(final UserRepository userRepository, final JwtUtils jwtUtils) {
        instance.userRepository = userRepository;
        instance.jwtUtils = jwtUtils;
        return instance;
    }

    public void initialize(UserFromTokenInDb constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    public boolean isValid(String token, ConstraintValidatorContext constraintValidatorContext) {
        try {
            return userFromTokenExistsInDB(token);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean userFromTokenExistsInDB(String token) throws ParseException {
        Integer tokenUserId = instance.jwtUtils.getUserIdFromStringToken(token);
        return instance.userRepository.existsById(tokenUserId);
    }
}
