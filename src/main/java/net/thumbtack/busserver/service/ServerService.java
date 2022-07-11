package net.thumbtack.busserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.thumbtack.busserver.dao.DebugDao;

@Service
public class ServerService {

    @Autowired
    private DebugDao serverDao;

    public void dataBaseCleanup() {
        serverDao.dataBaseCleanup();
    }
    
}
