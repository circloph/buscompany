package net.thumbtack.busserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceRequest {
    private Integer orderId;
    private String lastName;
    private String firstName;
    private String passport;
    private String place;
}

