package net.thumbtack.busserver.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

@Component
public class AnthroponymAdministrationValidator implements ConstraintValidator<AnthroponymAdministration, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl wrapperImpl = new BeanWrapperImpl(value);
        if (wrapperImpl.getPropertyValue("position") == null) {
            return true;
        }
        String lastname = (String) wrapperImpl.getPropertyValue("lastname");
        String firstname = (String) wrapperImpl.getPropertyValue("firstname");
        String patronymic = (String) wrapperImpl.getPropertyValue("patronymic");
        boolean onlyRussianAlphabetInLastname = lastname.matches("^[-а-яА-Я0-9\\s]+$");
        boolean onlyRussianAlphabetInFirstname = firstname.matches("^[-а-яА-Я0-9\\s]+$");
        boolean onlyRussianAlphabetInPatronymic = patronymic.matches("^[-а-яА-Я0-9\\s]+$");
        if (onlyRussianAlphabetInLastname && onlyRussianAlphabetInFirstname && onlyRussianAlphabetInPatronymic) {
            return true;
        }
        return false;
    }
}
