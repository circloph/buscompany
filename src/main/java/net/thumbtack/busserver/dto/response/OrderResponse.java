package net.thumbtack.busserver.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {

    private Integer orderId;
    private String tripId;
    private String fromStation;
    private String toStation;
    private String busName;
    private String date;
    private String start;
    private String duration;
    private Integer price;
    private Integer totalPrice;
    List<PassengerResponse> passengers;

}

