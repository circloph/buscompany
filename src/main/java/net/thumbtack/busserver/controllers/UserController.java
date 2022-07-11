package net.thumbtack.busserver.controllers;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import net.thumbtack.busserver.dto.request.RegistrationUpdateRequest;
import net.thumbtack.busserver.dto.response.UserResponse;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/authenticated")
    public String helloAuthenticatedUser(HttpServletRequest request) {
        return "hello authenticated user";
    }

    @PostMapping(path = "/api/admins", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse registrationAdmin(@Valid @RequestBody RegistrationUpdateRequest registrationAdmin,
            HttpServletRequest request, HttpServletResponse httpResponse) throws Exception {
        UserResponse response = userService.registrationAdministration(registrationAdmin, httpResponse);
        return response;
    }

    @PostMapping(path = "/api/clients", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)

    public UserResponse registrationClient(@Valid @RequestBody RegistrationUpdateRequest registrationClient, HttpServletResponse httpResponse) throws Exception {
        return userService.registrationClient(registrationClient, httpResponse);
    }

    @GetMapping(path = "/api/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse loginUser(@CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws Exception {
        return userService.loginUser(cookie, response);
    }

    @DeleteMapping(path = "/api/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public String logoutUser(@CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) {
        userService.logout(cookie, response);
        return new String("log out");
    }

    @DeleteMapping("/api/accounts")
    public String deleteUser(@CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws BusCompanyException, ServletException {
        userService.deleteUser(cookie);
        userService.logout(cookie, response);
        return new String();
    }

    @GetMapping(path = "/api/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse getInfoAboutYourself(@CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws Exception {
        return userService.getInfoAboutYourself(cookie, response);
    }

    @GetMapping(path = "/api/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserResponse> getAllClients(@CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws Exception {
        return userService.getAllClients(cookie, response);
    }

    @PutMapping(path = "/api/admins", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse updateAdmin(@RequestBody RegistrationUpdateRequest request, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response)
            throws Exception {
        return userService.updateAdministration(request, cookie, response);
    }

    @PutMapping(path = "/api/clients", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse updateClient(@RequestBody RegistrationUpdateRequest updateRequest, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response)
            throws Exception {
        updateRequest.setNumberPhone(updateRequest.getNumberPhone().replaceAll("-", ""));
        return userService.updateClient(updateRequest, cookie, response);
    }

}
