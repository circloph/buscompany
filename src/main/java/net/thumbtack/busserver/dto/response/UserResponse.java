package net.thumbtack.busserver.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
@Data
@JsonInclude(value = Include.NON_NULL)
public class UserResponse {
    private Integer id;
    private String lastname;
    private String firstname;
    private String patronymic;
    private String email;
    private String numberPhone;
    private String position;
    private String userType;

}
