package net.thumbtack.busserver.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LengthFieldValidator implements ConstraintValidator<LengthField, Object> {

    @Value("${max_name_length}")
    private int maxLength;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(value);
        String login = (String) beanWrapperImpl.getPropertyValue("login");
        String password = (String) beanWrapperImpl.getPropertyValue("password");
        String lastname = (String) beanWrapperImpl.getPropertyValue("lastname");
        String firstname = (String) beanWrapperImpl.getPropertyValue("firstname");
        String patronymic = (String) beanWrapperImpl.getPropertyValue("patronymic");
        if (login.length() <= maxLength && password.length() <= maxLength && lastname.length() <= maxLength
        && firstname.length() <= maxLength && patronymic.length() <= maxLength) {
            return true;
        }
        return false;
    }
}

