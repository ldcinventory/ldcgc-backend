package org.ldcgc.backend.validator.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ldcgc.backend.validator.UserValidation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

@Documented
@Constraint(validatedBy = UserValidation.class)
@Target( { PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserFromTokenInDb {

    String message() default "User id or user from token not found, or token is not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
