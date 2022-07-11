package net.thumbtack.busserver.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import net.thumbtack.busserver.dto.request.ScheduleRequest;

@Component
public class TripValidator implements ConstraintValidator<Trip, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl wrapperImpl = new BeanWrapperImpl(value);

        ScheduleRequest scheduleRequest = (ScheduleRequest) wrapperImpl.getPropertyValue("schedule");
        List<?> dates = (List<?>) wrapperImpl.getPropertyValue("dates");

        if (dates != null && scheduleRequest != null) {
            return false;
        }
        return true;
    }
    
}
