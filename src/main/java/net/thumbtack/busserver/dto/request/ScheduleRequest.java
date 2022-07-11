package net.thumbtack.busserver.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduleRequest {

    @NotNull(message = "EMPTY_FIELD_SCHEDULE")
    @NotEmpty(message = "EMPTY_FIELD_SCHEDULE")
    private String fromDate;
    @NotEmpty(message = "EMPTY_FIELD_SCHEDULE")
    @NotNull(message = "EMPTY_FIELD_SCHEDULE")
    private String toDate;
    @NotEmpty(message = "EMPTY_FIELD_SCHEDULE")
    @NotNull(message = "EMPTY_FIELD_SCHEDULE")
    private String period;
    
}
