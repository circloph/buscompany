package net.thumbtack.busserver.dto.request;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.thumbtack.busserver.validator.AnthroponymAdministration;
import net.thumbtack.busserver.validator.LengthField;
import net.thumbtack.busserver.validator.LengthPassword;

@Data
@AllArgsConstructor
@Builder
@AnthroponymAdministration
@LengthField
public class RegistrationUpdateRequest {
    private String lastname;
    private String firstname;
    private String patronymic;
    @Pattern(message = "INVALID_EMAIL_VALUE", regexp = "^[\\w\\.-_]{4,20}@\\w{3,10}\\.{1}\\w{2,4}$")
    private String email;
    @Pattern(message = "INVALID_NUMBER_PHONE_VALUE", regexp = "^(\\+7|8)\\-?\\d{3}\\-?\\d{3}\\-?\\d{2}\\-?\\d{2}$")
    private String numberPhone;
    private String position;
    @Pattern(message = "INVALID_LOGIN_VALUE", regexp = "^[а-яА-Яa-zA-Z0-9]+$")
    private String login;
    @LengthPassword
    private String password;
    private String oldPassword;
    private String newPassword;

}