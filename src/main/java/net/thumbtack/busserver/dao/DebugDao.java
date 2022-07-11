package net.thumbtack.busserver.dao;

import org.springframework.stereotype.Component;

@Component
public interface DebugDao {

    void dataBaseCleanup();
    
}
