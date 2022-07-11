package net.thumbtack.busserver.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = TripValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trip {
    String message() default "MUTUALLY_EXCLUSIVE_FIELDS";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
