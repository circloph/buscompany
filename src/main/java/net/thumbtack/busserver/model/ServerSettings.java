package net.thumbtack.busserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerSettings {

    private Integer maxNameLength;
    private Integer minPasswordLength;


    
}
