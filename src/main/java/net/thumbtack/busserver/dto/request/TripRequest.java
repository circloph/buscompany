package net.thumbtack.busserver.dto.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.thumbtack.busserver.validator.Trip;

@Data
@AllArgsConstructor
@Builder
@Trip
public class TripRequest {
    
    private String busName;
    private String fromStation;
    private String toStation;
    @Pattern(message = "INVALID_FORMAT_START" , regexp = "^(0[0-9]|1[0-9]|2[0-3]|[0-9]):[0-5][0-9]$")
    private String start;
    @Pattern(message = "INVALID_FORMAT_DURATION" , regexp = "^(0[0-9]|1[0-9]|2[0-3]|[0-9]):[0-5][0-9]$")
    private String duration;
    private Integer price;
    @Valid
    private ScheduleRequest schedule;
    
    private List<@Pattern(message = "INVALID_FORMAT_DATE", regexp = "^\\d{4}-\\d{2}-\\d{2}$") String> dates;

}