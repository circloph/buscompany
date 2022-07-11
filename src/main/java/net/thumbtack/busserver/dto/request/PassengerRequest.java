package net.thumbtack.busserver.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PassengerRequest {

    private Integer id;
    @NotEmpty(message = "INVALID_PASSENGER_DATA")
    @NotBlank(message = "INVALID_PASSENGER_DATA")
    private String firstName;
    @NotBlank(message = "INVALID_PASSENGER_DATA")
    @NotEmpty(message = "INVALID_PASSENGER_DATA")
    private String lastName;
    @NotBlank(message = "INVALID_PASSENGER_DATA")
    @NotEmpty(message = "INVALID_PASSENGER_DATA")
    private String passport;   
    
}
