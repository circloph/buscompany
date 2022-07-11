package net.thumbtack.busserver.daoImpl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import net.thumbtack.busserver.dao.OrderDao;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.exception.ServerException;
import net.thumbtack.busserver.model.Order;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Passenger;
import net.thumbtack.busserver.model.Place;

@Component
public class OrderDaoImpl extends DaoImplBase implements OrderDao {

    @Override
    public void insertOrder(Order order) throws BusCompanyException {
        try (SqlSession session = getSession()) {
            try {
                if (getTripMapper(session).checkAndUpdateNumberOfPlaces(order.getPassengers().size(), order) == 1) {

                    getOrderMapper(session).insertOrder(order);
                    order.getPassengers().stream().forEach(passenger -> getPassengerMapper(session).insertPassenger(passenger));
                    order.getPassengers().stream().forEach(passenger -> getOrderMapper(session).insertOrderIdAndPassengerId(order, passenger));
                } else {
                    throw new BusCompanyException(ErrorCode.NO_PLACE);
                }
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public Order getOrderById(Integer id) {
        Order order = null;
        try (SqlSession session = getSession()) {
            try {
                order = getOrderMapper(session).getOrderById(id);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return order;
    }

    @Override
    public List<Order> getListOrders(Parameters parameters) {
        List<Order> order = null;
        try (SqlSession session = getSession()) {
            try {
                order = getOrderMapper(session).getListOrderForAll(parameters);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return order;
    }

    @Override
    public List<Integer> getBusyPlaces(Order order) {
        List<Integer> places = null;
        try (SqlSession session = getSession()) {
            try {
                places = getPlaceMapper(session).getBusyPlaces(order);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return places;
    }

    @Override
    public Passenger getPassenger(Passenger passenger) {
        Passenger receivedPassenger = null;
        try (SqlSession session = getSession()) {
            try {
                receivedPassenger = getPassengerMapper(session).getPassenger(passenger);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return receivedPassenger;
    }

    @Override
    public boolean selectPlace(Place place) {
        boolean result;
        try (SqlSession session = getSession()) {
            try {
                result = getPlaceMapper(session).selectPlace(place);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return result;
    }

    @Override
    public Integer getNumberOfFreePlaces(Order order) {
        Integer numberOccipiedPlaces = null;
        try (SqlSession session = getSession()) {
            try {
                numberOccipiedPlaces = getPlaceMapper(session).getNumberOfFreePlaces(order);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return numberOccipiedPlaces;
    }

    @Override
    public void deleteOrderByUserId(Integer id) {
        try (SqlSession session = getSession()) {
            try {
                getOrderMapper(session).deleteOrderByUserId(id);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }


    @Override
    public void removePlaceIfExist(Integer orderId, Integer passengerId, Integer place) {
        try (SqlSession session = getSession()) {
            try {
                getPlaceMapper(session).removePlaceIfExist(orderId, passengerId, place);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public boolean checkPlace(Order order, Integer place) {
        boolean result;
        try (SqlSession session = getSession()) {
            try {
                result = getPlaceMapper(session).checkPlace(order, place);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return result;
    }
}
