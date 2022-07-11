package net.thumbtack.busserver.dto.request;

import java.util.List;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRequest {

    private Integer tripId;
    private String date;
    @Valid
    private List<PassengerRequest> passengers;

}

