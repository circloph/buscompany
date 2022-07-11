package net.thumbtack.busserver.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Value;

public class LengthPasswordValidator implements ConstraintValidator<LengthPassword, String> {

    @Value("${min_password_length}")
    private int minLength;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (value.length() < minLength) {
            return false;
        }
        return true;
    }
    
}
