package org.ldcgc.backend.validator;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;

import java.text.ParseException;

@RequiredArgsConstructor
public class UserValidation implements ConstraintValidator<UserFromTokenInDb, String> {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    private static UserValidation INSTANCE;

    @PostConstruct
    public void init() {
        UserValidation.INSTANCE = this;
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

    public static boolean userFromTokenExistsInDB(String token) throws ParseException {
        Integer tokenUserId = INSTANCE.jwtUtils.getUserIdFromStringToken(token);
        return INSTANCE.userRepository.existsById(tokenUserId);
    }
}
