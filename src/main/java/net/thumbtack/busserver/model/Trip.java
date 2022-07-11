package net.thumbtack.busserver.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    private Integer id;
    private Integer userId;
    private String fromStation;
    private String toStation;
    private String start;
    private String duration;
    private Integer price;
    private Bus bus;
    private Boolean approved;
    private Schedule schedule;
    private List<CustomDate> dates;

}
