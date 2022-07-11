package net.thumbtack.busserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bus {

    private Integer id;
    private String busName;
    private Integer placeCount;
    
}
