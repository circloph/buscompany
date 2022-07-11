package net.thumbtack.busserver.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target(ElementType.TYPE)
@Constraint(validatedBy = AnthroponymAdministrationValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnthroponymAdministration {
    String message() default "INVALID_ANTHROPONYM_VALUES";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

