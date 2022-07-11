package net.thumbtack.busserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.thumbtack.busserver.model.ServerSettings;

@RestController
public class SettingsController {

    @Autowired
    private Environment env;
    
    @GetMapping(path = "/api/settings")
    public ServerSettings getSettings() {
        return new ServerSettings(Integer.valueOf(env.getProperty("max_name_length")), Integer.valueOf(env.getProperty("min_password_length")));
    }
}
