package net.thumbtack.busserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerResponse {

    private Integer id;
    private String firstName;
    private String lastName;
    private String passport;   
    
}
