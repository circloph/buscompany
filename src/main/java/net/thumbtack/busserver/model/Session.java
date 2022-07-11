package net.thumbtack.busserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Session {

    String sessionId;
    String expiration;
    
}
