package net.thumbtack.busserver.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class TripResponse {
    
    private Integer tripId;
    private String fromStation;
    private String toStation;
    private String start;
    private String duration;
    private Integer price;
    private BusResponse bus;
    private Boolean approved;
    private ScheduleResponse schedule;
    private List<String> dates;

}
