package net.thumbtack.busserver.controllers;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.thumbtack.busserver.dto.request.TripRequest;
import net.thumbtack.busserver.dto.response.BusResponse;
import net.thumbtack.busserver.dto.response.TripResponse;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.service.TripService;

@RestController
public class TripController {

    @Autowired
    private TripService tripService;

    @GetMapping(path = "/api/buses")
    public List<BusResponse> getAllBuses(@CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) {
        return tripService.getAllBuses(cookie, response);
    }

    @PostMapping(path = "/api/trips", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TripResponse addTrip(@Valid @RequestBody TripRequest tripRequest, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws ParseException, BusCompanyException {
        return tripService.addTrip(tripRequest, cookie, response);
    }

    @PutMapping(path = "/api/trips/{tripId}")
    public TripResponse updateTrip(@PathVariable Integer tripId, @RequestBody TripRequest tripRequest, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws ParseException, BusCompanyException {
        return tripService.updateTrip(tripId, tripRequest, cookie, response);
    }

    @DeleteMapping(path = "/api/trips/{tripId}")
    public String deleteTrip(@PathVariable Integer tripId, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        return tripService.deleteTrip(tripId, cookie, response);
    }

    @GetMapping(path = "/api/trips/{tripId}")
    public TripResponse getTrip(@PathVariable Integer tripId, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) {
        return tripService.getTrip(tripId, cookie, response);
    }

    @PutMapping(path = "/api/trips/{tripId}/approve")
    public TripResponse approveTrip(@PathVariable Integer tripId, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) {
        return tripService.approveTrip(tripId, cookie, response);
    }

    @GetMapping(path = "/api/trips")
    public List<TripResponse> getListTrips(Principal principal, @RequestParam(required = false) String fromStation,
    @RequestParam(required = false) String toStation, @RequestParam(required = false) String busName,
    @RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) {
        Parameters parameters = new Parameters(null, fromStation, toStation, busName, fromDate, toDate);
        return tripService.getTrips(parameters, cookie, response);
    }
    
}
