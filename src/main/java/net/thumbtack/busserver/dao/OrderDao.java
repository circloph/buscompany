package net.thumbtack.busserver.dao;

import java.util.List;

import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Order;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Passenger;
import net.thumbtack.busserver.model.Place;

public interface OrderDao {
    
    void insertOrder(Order order) throws BusCompanyException;
    
    Order getOrderById(Integer id);

    List<Order> getListOrders(Parameters parameters);

    Passenger getPassenger(Passenger passenger);

    List<Integer> getBusyPlaces(Order order);
    
    boolean selectPlace(Place place);

    Integer getNumberOfFreePlaces(Order order);

    void deleteOrderByUserId(Integer userId);

    void removePlaceIfExist(Integer orderId, Integer passengerId, Integer place);

    boolean checkPlace(Order order, Integer place);

}
