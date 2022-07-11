package net.thumbtack.busserver.daoImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.CustomDate;
import net.thumbtack.busserver.model.Order;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Passenger;
import net.thumbtack.busserver.model.Place;
import net.thumbtack.busserver.model.Trip;
import net.thumbtack.busserver.model.User;

public class OrderDaoImplTest {

    private static OrderDaoImpl orderDaoImpl;
    private static UserDaoImpl userDaoImpl;
    private static TripDaoImpl tripDaoImpl;

    @BeforeAll
    static void setUp() {
        orderDaoImpl = new OrderDaoImpl();
        userDaoImpl = new UserDaoImpl();
        tripDaoImpl = new TripDaoImpl();
    }

    @BeforeEach
    void dataBaseCleanup() {
        DebugDaoImpl serverDaoImpl = new DebugDaoImpl();
        serverDaoImpl.dataBaseCleanup();
    }

    public User createAndInsertUser() {
        Client user  = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
        userDaoImpl.insertClient(user);
        return user;
    }

    public Trip createAndInsertTrip() {
        List<CustomDate> dates = new ArrayList<>();
        dates.add(new CustomDate("2024-06-30"));
        Trip trip = Trip.builder().fromStation("Lenina").toStation("Pushkina")
        .start("12:00").duration("02:00").price(1000).bus(tripDaoImpl.getBusByName("MERCEDES")).dates(dates).build();
        tripDaoImpl.insertTrip(trip);
        return trip;
    }

    public Order createAndInsertOrder(Trip trip, User user) throws BusCompanyException {
        List<Passenger> passengers = new ArrayList<>();
        passengers.add(new Passenger("Petya", "Pupkin", "262056"));
        passengers.add(new Passenger("Vasya", "Ivanov", "756395"));
        Order order = Order.builder().tripId(trip.getId()).userId(user.getId()).date("2024-06-30").passengers(passengers).build();
        order.setUserId(user.getId());
        orderDaoImpl.insertOrder(order);
        return order;
    }

    @Test
    void testDeleteOrder() throws BusCompanyException {
        User user = createAndInsertUser();
        Trip trip = createAndInsertTrip();
        Order order = createAndInsertOrder(trip, user);
        Order orderFromDB = orderDaoImpl.getOrderById(order.getId());
        assertTrue(orderFromDB != null);
        orderDaoImpl.deleteOrderByUserId(order.getId());
        Order orderFromDBAfterDelete = orderDaoImpl.getOrderById(order.getId());
        assertTrue(orderFromDBAfterDelete == null);
    }

    @Test
    void testGetListOrders() throws BusCompanyException {
        User user = createAndInsertUser();
        Trip trip = createAndInsertTrip();
        tripDaoImpl.approveTrip(trip.getId());
        createAndInsertOrder(trip, user);
        createAndInsertOrder(trip, user);

        int sizeOrderListWithoutParameters =  orderDaoImpl.getListOrders(new Parameters(user.getId(), null, null, null, null, null)).size();
        assertEquals(2, sizeOrderListWithoutParameters);
        
    }

    @Test
    void testGetNumberOccupiedPlaces() throws BusCompanyException {
        User user = createAndInsertUser();
        Trip firstTrip = createAndInsertTrip();
        Trip secondTrip = createAndInsertTrip();
        Order firstOrder = createAndInsertOrder(firstTrip, user);
        Order secondOrder = createAndInsertOrder(secondTrip, user);
        assertEquals(22, orderDaoImpl.getNumberOfFreePlaces(secondOrder));
        assertEquals(22, orderDaoImpl.getNumberOfFreePlaces(firstOrder));
    }

    @Test
    void testGetOrderById() throws BusCompanyException {
        User user = createAndInsertUser();
        Trip trip = createAndInsertTrip();
        Order order = createAndInsertOrder(trip, user);
        order.setBus(tripDaoImpl.getBusByName("MERCEDES"));
        order.setDuration(trip.getDuration());
        order.setFromStation(trip.getFromStation());
        order.setToStation(trip.getToStation());
        order.setStart(trip.getStart());
        order.setPrice(trip.getPrice());

        assertEquals(order, orderDaoImpl.getOrderById(order.getId()));
    }

    @Test
    void testGetPassenger() throws BusCompanyException {
        User user = createAndInsertUser();
        Trip trip = createAndInsertTrip();
        Order order = createAndInsertOrder(trip, user);
        
        assertTrue(orderDaoImpl.getPassenger(order.getPassengers().get(0)).getId() != null);
    }

    @Test
    void testGetPlacesFree() throws BusCompanyException {
        User user = createAndInsertUser();
        Trip trip = createAndInsertTrip();
        Order order = createAndInsertOrder(trip, user);
        List<Integer> busyPlaces = new ArrayList<>();
        assertEquals(busyPlaces, orderDaoImpl.getBusyPlaces(order));
    }

    @Test
    void testInsertOrder() throws BusCompanyException {
        User user = createAndInsertUser();
        Trip trip = createAndInsertTrip();
        Order order = createAndInsertOrder(trip, user);
        assertTrue(order.getId() != null);
        assertEquals(1, order.getId());
    }

    @Test
    void testRemovePlaceIfExist() throws BusCompanyException {
        User user = createAndInsertUser();
        Trip trip = createAndInsertTrip();
        Order order = createAndInsertOrder(trip, user);

        Passenger vasya = order.getPassengers().get(0);

        Place firstPlace = Place.builder().orderId(order.getId()).passenger(vasya).place(20).build();
        Place secondPlace = Place.builder().orderId(order.getId()).passenger(vasya).place(21).build();

        orderDaoImpl.selectPlace(firstPlace);
        orderDaoImpl.selectPlace(secondPlace);
        assertEquals(2, orderDaoImpl.getBusyPlaces(order).size());
        orderDaoImpl.removePlaceIfExist(order.getId(), vasya.getId(), secondPlace.getPlace());
        assertEquals(1, orderDaoImpl.getBusyPlaces(order).size());
    }

    @Test
    void testSelectPlace() throws BusCompanyException {
        User user = createAndInsertUser();
        Trip trip = createAndInsertTrip();
        Order order = createAndInsertOrder(trip, user);
        Passenger vasya = order.getPassengers().get(0);

        assertTrue(orderDaoImpl.getBusyPlaces(order).isEmpty());
        orderDaoImpl.selectPlace(Place.builder().orderId(order.getId()).passenger(vasya).place(20).build());
        assertFalse(orderDaoImpl.getBusyPlaces(order).isEmpty());
        assertEquals(1, orderDaoImpl.getBusyPlaces(order).size());
    }

}
