package net.thumbtack.busserver.controllers;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.thumbtack.busserver.dto.request.OrderRequest;
import net.thumbtack.busserver.dto.request.PlaceRequest;
import net.thumbtack.busserver.dto.response.OrderResponse;
import net.thumbtack.busserver.dto.response.PlaceResponse;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.service.OrderService;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(path = "/api/orders")
    public OrderResponse orderingTicket(@Valid @RequestBody OrderRequest orderRequest, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        return orderService.orderingTicket(orderRequest, cookie, response);
    }

    @GetMapping(path = "/api/orders")
    public List<OrderResponse> getOrdersList(@CookieValue(name = "JSESSIONID") Cookie cookie, @RequestParam(required = false) String fromStation,
            @RequestParam(required = false) String toStation, @RequestParam(required = false) String busName,
            @RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Integer clientId, HttpServletResponse response) {
        Parameters parameters = new Parameters(clientId, fromStation, toStation, busName, fromDate, toDate);
        return orderService.getOrdersList(parameters, cookie, response);
    }

    @GetMapping(path = "/api/places/{orderId}")
    public List<Integer> getFreePlaces(@PathVariable Integer orderId, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        return orderService.getFreePlaces(orderId, cookie, response);
    }

    @PostMapping(path = "/api/places")
    public PlaceResponse placeSelect(@RequestBody PlaceRequest placeRequest, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        return orderService.selectPlace(placeRequest, cookie, response);
    }

    @DeleteMapping(path = "/api/orders/{id}")
    public String cancelOrder(@PathVariable Integer id, @CookieValue(name = "JSESSIONID") Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        return orderService.cancelOrder(id, cookie, response);
    }
}
