package org.ldcgc.backend.base.annotation;

import jakarta.validation.ConstraintValidator;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.validator.UserValidation;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;

@RequiredArgsConstructor
public class TestConstrainValidationFactory extends SpringWebConstraintValidatorFactory {

    private final WebApplicationContext ctx;

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        ConstraintValidator instance = super.getInstance(key);
        if (instance instanceof UserValidation) {
            instance = ctx.getBean(UserValidation.class);
        }
        return (T) instance;
    }

    @Override
    protected WebApplicationContext getWebApplicationContext() {
        return ctx;
    }
}
