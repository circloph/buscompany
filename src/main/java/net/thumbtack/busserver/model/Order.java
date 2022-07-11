package net.thumbtack.busserver.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    private Integer id;
    private Integer tripId;
    private Integer userId;
    private String fromStation;
    private String toStation;
    private Bus bus;
    private String date;
    private String start;
    private String duration;
    private Integer price;
    private List<Passenger> passengers;
    
}
