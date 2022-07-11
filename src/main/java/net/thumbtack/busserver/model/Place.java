package net.thumbtack.busserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Place {

    @NonNull
    private Integer orderId;
    private Passenger passenger;
    @NonNull
    private Integer place;
    
}
