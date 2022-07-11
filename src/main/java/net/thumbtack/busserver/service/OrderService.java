package net.thumbtack.busserver.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.thumbtack.busserver.dao.OrderDao;
import net.thumbtack.busserver.dao.TripDao;
import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.dto.mappers.OrderDtoMapper;
import net.thumbtack.busserver.dto.request.OrderRequest;
import net.thumbtack.busserver.dto.request.PlaceRequest;
import net.thumbtack.busserver.dto.response.OrderResponse;
import net.thumbtack.busserver.dto.response.PlaceResponse;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.CustomDate;
import net.thumbtack.busserver.model.Order;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Passenger;
import net.thumbtack.busserver.model.Place;
import net.thumbtack.busserver.model.Session;
import net.thumbtack.busserver.model.Trip;
import net.thumbtack.busserver.model.User;
import net.thumbtack.busserver.security.SessionProvider;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private TripDao tripDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SessionProvider sessionProvider;

    private OrderDtoMapper orderMapper = Mappers.getMapper(OrderDtoMapper.class);

    @Value("${user_idle_timeout}")
    private int userIdleTimeout;

    @Autowired
    private static List<Integer> MERCEDES;
    @Autowired
    private static List<Integer> SCANIA;

    static {
        MERCEDES = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            MERCEDES.add(i + 1);
        }
        SCANIA = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            SCANIA.add(i + 1);
        }
    }

    @Transactional
    public OrderResponse orderingTicket(OrderRequest orderRequest, Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        Order order = null;
        Trip tripFromDB = tripDao.getTripById(orderRequest.getTripId());
        Optional<CustomDate> matchingDate = tripFromDB.getDates().stream().filter(date -> date.getDayName().equals(orderRequest.getDate())).findFirst();
        if (matchingDate.isPresent() && tripFromDB.getDates().contains(matchingDate.get())) {
            order = orderMapper.orderRequestToOrder(orderRequest, tripFromDB);
            String sessionId = cookie.getValue();
            Integer userId = userDao.getUserIdBySessionId(sessionId); 
            order.setUserId(userId);
            orderDao.insertOrder(order);  
            userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
            sessionProvider.extendExpirationCookie(cookie, response);
            return orderMapper.orderToOrderResponse(order);
        }
        throw new BusCompanyException(ErrorCode.INVALID_DATE_VALUE);
    }


    public List<OrderResponse> getOrdersList(Parameters parameters, Cookie cookie, HttpServletResponse response) {
        List<Order> orders = null;
        String sessionId = cookie.getValue();
        User user = userDao.getUserBySessionId(sessionId);
        if (user instanceof Client) {
            if (parameters.getUserId() == null) {
                parameters.setUserId(user.getId());
                orders = orderDao.getListOrders(parameters);
            }
        } else {
            orders = orderDao.getListOrders(parameters);
        }
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);
        return orderMapper.listOrderToOrderResponse(orders);
    }

    public List<Integer> getFreePlaces(Integer orderId, Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        List<Integer> places = null;
        Order order = orderDao.getOrderById(orderId);
        String sessionId = cookie.getValue();
        User user = userDao.getUserBySessionId(sessionId);
        if (order != null && order.getUserId() == user.getId()) {
            if (order.getBus().getBusName().equals("MERCEDES")) {
                places = new ArrayList<>(MERCEDES);
            } else {
                places = new ArrayList<>(SCANIA);
            }
            places.removeAll(orderDao.getBusyPlaces(order));
            userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
            sessionProvider.extendExpirationCookie(cookie, response);
            return places;
        }
        throw new BusCompanyException(ErrorCode.INVALID_ORDER_ID);
    }

    @Transactional
    public PlaceResponse selectPlace(PlaceRequest request, Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        Place place = null;
        String sessionId = cookie.getValue();
        Order order = orderDao.getOrderById(request.getOrderId());
        Passenger passenger = orderDao.getPassenger(new Passenger(request.getFirstName(), request.getLastName(), request.getPassport()));
        if (order.getPassengers().contains(passenger)) {
            place = orderMapper.placeRequestAndPassengerToPlace(request, passenger);
            if (!orderDao.checkPlace(order, Integer.parseInt(request.getPlace()))) {
                if (orderDao.selectPlace(place)) {
                    orderDao.removePlaceIfExist(order.getId(), passenger.getId(), place.getPlace());
                    String ticket = new String("Билет " + order.getTripId() + "_" + request.getPlace());
                    userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
                    sessionProvider.extendExpirationCookie(cookie, response);
                    return orderMapper.toPlaceResponse(place, ticket);
                }
            }
            throw new BusCompanyException(ErrorCode.BUSY_PLACE);
        }
        throw new BusCompanyException(ErrorCode.INVALID_PASSENGER_DATA);
    }

    public String cancelOrder(Integer orderId, Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        String sessionId = cookie.getValue();
        User user = userDao.getUserBySessionId(sessionId);
        Order order = orderDao.getOrderById(orderId);
        if (order != null) {
            if (order.getUserId() == user.getId()) {
                orderDao.deleteOrderByUserId(orderId);
                userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
                sessionProvider.extendExpirationCookie(cookie, response);
                return "{}";
            }
        }
        throw new BusCompanyException(ErrorCode.INVALID_ORDER_ID);
    
    }

}
