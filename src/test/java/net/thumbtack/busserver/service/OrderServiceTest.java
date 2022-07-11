package net.thumbtack.busserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.thumbtack.busserver.MaintaskApplication;
import net.thumbtack.busserver.dao.OrderDao;
import net.thumbtack.busserver.dao.TripDao;
import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.dto.request.OrderRequest;
import net.thumbtack.busserver.dto.request.PassengerRequest;
import net.thumbtack.busserver.dto.request.PlaceRequest;
import net.thumbtack.busserver.dto.response.OrderResponse;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Bus;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.CustomDate;
import net.thumbtack.busserver.model.Order;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Passenger;
import net.thumbtack.busserver.model.Place;
import net.thumbtack.busserver.model.Role;
import net.thumbtack.busserver.model.Schedule;
import net.thumbtack.busserver.model.Trip;
import net.thumbtack.busserver.model.User;
import net.thumbtack.busserver.security.SessionProvider;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MaintaskApplication.class)
public class OrderServiceTest {

    @MockBean
    private TripDao tripDao;
    
    @MockBean
    private UserDao userDao;

    @MockBean
    private OrderDao orderDao;

    @MockBean
    private SessionProvider sessionProvider;

    @Autowired
    private OrderService orderService;


    @Test
    void testCancelOrder() throws BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        Order order = Order.builder().userId(1).build();
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        client.setId(1);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        when(orderDao.getOrderById(1)).thenReturn(order);
        String response = orderService.cancelOrder(1, cookie, mock(HttpServletResponse.class));
        verify(orderDao, times(1)).deleteOrderByUserId(1);
        assertEquals("{}", response);
    }

    @Test
    void testCancelOrder_BusCompanyException_InvalidOrderId() throws BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        Order order = null;
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        client.setId(1);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        when(orderDao.getOrderById(1)).thenReturn(order);

        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> orderService.cancelOrder(1, cookie, mock(HttpServletResponse.class)));
        verify(orderDao, times(0)).deleteOrderByUserId(1);
        assertEquals("invalid order id value", exception.getError().getMessage());
    }

    @Test
    void testGetFreePlaces() throws BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        Order order = Order.builder().userId(1).bus(new Bus(1, "MERCEDES", 24)).build();
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        client.setId(1);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        when(orderDao.getOrderById(1)).thenReturn(order);

        List<Integer> places = new ArrayList<>();
        places.add(1);
        places.add(2);
        places.add(3);
        places.add(4);
        places.add(5);

        when(orderDao.getBusyPlaces(Mockito.any(Order.class))).thenReturn(places);

        List<Integer> freePlaces = orderService.getFreePlaces(1, cookie, mock(HttpServletResponse.class));
        assertEquals(19, freePlaces.size());
    }

    @Test
    void testGetFreePlaces_BusCompanyException_InvalidOrderId() throws BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        Order order = null;
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        client.setId(1);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        when(userDao.getUserByLogin("ivanov")).thenReturn(client);
        when(orderDao.getOrderById(1)).thenReturn(order);
        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> orderService.getFreePlaces(1, cookie, mock(HttpServletResponse.class)));
        assertEquals("invalid order id value", exception.getError().getMessage());
    }

    @Test
    void testGetOrdersList() {
        Cookie cookie = mock(Cookie.class);
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        client.setId(1);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        when(userDao.getUserByLogin("ivanov")).thenReturn(client);

        Parameters parameters = new Parameters(null, null, null, null, "2024-06-06");
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, 1, 1, "lenina", "Mira", new Bus(), "2024-01-01", "22:00", "08:00", 500, new ArrayList<Passenger>()));
        when(orderDao.getListOrders(parameters)).thenReturn(orders);
        List<OrderResponse> ordersResponse = orderService.getOrdersList(parameters, cookie, mock(HttpServletResponse.class));
        assertEquals(1, ordersResponse.size());
    }

    @Test
    void testOrderingTicket() throws BusCompanyException {
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        client.setId(1);
        Cookie cookie = mock(Cookie.class);
        List<CustomDate> dates = new ArrayList<>();
        dates.add(new CustomDate("2024-06-06"));
        dates.add(new CustomDate("2024-06-07"));
        Trip trip = new Trip(1, client.getId(), "Lenina", "Himikov", "16:00", "02:00", 1000, new Bus(1, "MERCEDES", 24), true, new Schedule(), dates);

        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        when(userDao.getUserByLogin("ivanov")).thenReturn(client);
        OrderRequest orderRequest = new OrderRequest(1, "2024-06-06", new ArrayList<PassengerRequest>());
        when(tripDao.getTripById(1)).thenReturn(trip);
        when(orderDao.getNumberOfFreePlaces(Mockito.any(Order.class))).thenReturn(19);
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((Order)args[0]).setId(1);
            return null;
        }).when(orderDao).insertOrder(Mockito.any(Order.class));
        OrderResponse orderResponse = orderService.orderingTicket(orderRequest, cookie, mock(HttpServletResponse.class));
        assertEquals(1, orderResponse.getOrderId());
    }

    @Test
    void testOrderingTicket_BusCompanyException_NoPlace() throws BusCompanyException {
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        client.setId(1);
        Cookie cookie = mock(Cookie.class);
        List<CustomDate> dates = new ArrayList<>();
        dates.add(new CustomDate("2024-06-06"));
        dates.add(new CustomDate("2024-06-07"));
        Trip trip = new Trip(1, client.getId() , "Lenina", "Himikov", "16:00", "02:00", 1000, new Bus(1, "MERCEDES", 24), true, new Schedule(), dates);
        PassengerRequest passengerRequest = new PassengerRequest(1, "Valya", "Pupkina", "777777");
        List<PassengerRequest> passengerRequests = new ArrayList<PassengerRequest>();
        passengerRequests.add(passengerRequest);
        
        OrderRequest orderRequest = new OrderRequest(1, "2024-06-06", passengerRequests);
        when(cookie.getValue()).thenReturn("sessionId");
        when(tripDao.getTripById(1)).thenReturn(trip);
        when(orderDao.getNumberOfFreePlaces(Mockito.any(Order.class))).thenReturn(0);
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        doThrow(new BusCompanyException(ErrorCode.NO_PLACE)).when(orderDao).insertOrder(Mockito.any(Order.class));
        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> orderService.orderingTicket(orderRequest, cookie, mock(HttpServletResponse.class)));
        assertEquals("no place", exception.getError().getMessage());
    }

    @Test
    void testOrderingTicket_BusCompanyException_InvalidDate() throws BusCompanyException {
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        client.setId(1);
        Cookie cookie = mock(Cookie.class);
        List<CustomDate> dates = new ArrayList<>();
        dates.add(new CustomDate("2024-06-06"));
        dates.add(new CustomDate("2024-06-07"));
        Trip trip = new Trip(1, client.getId(), "Lenina", "Himikov", "16:00", "02:00", 1000, new Bus(1, "MERCEDES", 24), true, new Schedule(), dates);
        PassengerRequest passengerRequest = new PassengerRequest(1, "Valya", "Pupkina", "777777");
        List<PassengerRequest> passengerRequests = new ArrayList<PassengerRequest>();
        passengerRequests.add(passengerRequest);
        
        OrderRequest orderRequest = new OrderRequest(1, "2024-09-06", passengerRequests);
        when(tripDao.getTripById(1)).thenReturn(trip);
        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> orderService.orderingTicket(orderRequest, cookie, mock(HttpServletResponse.class)));
        assertEquals("there is no such date on the trip", exception.getError().getMessage());
    }


    @Test
    void testSelectPlace() throws BusCompanyException {
        PlaceRequest place = new PlaceRequest(1, "Pupkina", "Valya", "777777", "1");

        Cookie cookie = mock(Cookie.class);
        Passenger firstPassenger = new Passenger(1, "Valya", "Pupkina", "777777");
        List<Passenger> passengers = new ArrayList<Passenger>();
        passengers.add(firstPassenger);

        Order order = Order.builder().tripId(1).passengers(passengers).build();
        when(orderDao.getOrderById(1)).thenReturn(order);
        when(orderDao.getPassenger(Mockito.any(Passenger.class))).thenReturn(firstPassenger);
        when(orderDao.checkPlace(Mockito.any(Order.class), Mockito.anyInt())).thenReturn(false);
        when(orderDao.selectPlace(Mockito.any(Place.class))).thenReturn(true);
        orderService.selectPlace(place, cookie, mock(HttpServletResponse.class));
        verify(orderDao, times(1)).selectPlace(Mockito.any(Place.class));

    }

    @Test
    void testSelectPlace_BusCompanyException_InvalidPassengerData() throws BusCompanyException {
        PlaceRequest place = new PlaceRequest(1, "Pupkina", "Valya", "777777", "1");

        Cookie cookie = mock(Cookie.class);
        Passenger firstPassenger = new Passenger(1, "Valya", "Pupkina", "777777");
        List<Passenger> passengers = new ArrayList<Passenger>();
        passengers.add(firstPassenger);

        Passenger wrongPassenger = new Passenger(3, "Georgiy", "Lomov", "123456");

        Order order = Order.builder().tripId(1).passengers(passengers).build();
        when(orderDao.getOrderById(1)).thenReturn(order);
        when(orderDao.getPassenger(Mockito.any(Passenger.class))).thenReturn(wrongPassenger);
        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> orderService.selectPlace(place, cookie, mock(HttpServletResponse.class)));
        assertEquals("there is no such passenger in the order", exception.getError().getMessage());
    }
}
