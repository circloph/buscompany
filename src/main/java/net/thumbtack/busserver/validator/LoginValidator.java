package net.thumbtack.busserver.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<Login, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.equals("")) {
            return false;
        }
        boolean onlyRussianLatinAlphabetAndNumbersInLogin = value.matches("^[а-яА-Яa-zA-Z0-9]+$");
        return onlyRussianLatinAlphabetAndNumbersInLogin;
    }
    
}

