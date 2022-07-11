package net.thumbtack.busserver.dto.response;

import lombok.Data;

@Data
public class PlaceResponse {

    private Integer orderId;
    private String ticket;
    private String lastName;
    private String firstName;
    private String passport;
    private String place;
    
}
