package net.thumbtack.busserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Parameters {

    private Integer userId;
    private String fromStation;
    private String toStation;
    private String busName;
    private String fromDate;
    private String toDate;



    public Parameters(String fromStation, String toStation, String busName, String fromDate, String toDate) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.busName = busName;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    
}
