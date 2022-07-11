package net.thumbtack.busserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusResponse {

    private Integer id;
    private String busName;
    private Integer placeCount;
    
}
