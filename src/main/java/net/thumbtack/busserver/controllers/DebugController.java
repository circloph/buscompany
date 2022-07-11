package net.thumbtack.busserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import net.thumbtack.busserver.service.ServerService;

@RestController
public class DebugController {
    
    @Autowired
    private ServerService serverService;

    @PostMapping(path = "/api/debug/clear")
    public void dataBaseCleanup() {
        serverService.dataBaseCleanup();
    }
    
}
