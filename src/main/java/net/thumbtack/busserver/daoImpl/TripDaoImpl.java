package net.thumbtack.busserver.daoImpl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import net.thumbtack.busserver.dao.TripDao;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.ServerException;
import net.thumbtack.busserver.model.Bus;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Trip;

@Component
public class TripDaoImpl extends DaoImplBase implements TripDao {

    @Override
    public List<Bus> getAllBuses() {
        List<Bus> buses = null;
        try (SqlSession session = getSession()) {
            try {
                buses = getBusMapper(session).getAllBuses();
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return buses;
    }

    @Override
    public void insertTrip(Trip trip) {
        try (SqlSession session = getSession()) {
            try {
                getTripMapper(session).insertTrip(trip);
                getScheduleMapper(session).insertSchedule(trip.getId(), trip.getSchedule());
                trip.getDates().stream().forEach(date -> getDateMapper(session).insertDate(date));
                trip.getDates().stream().forEach(date -> getTripMapper(session).insertDatesToTrip(trip, date, trip.getBus()));
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public Bus getBusByName(String name) {
        Bus bus = null;
        try (SqlSession session = getSession()) {
            try {
                bus = getBusMapper(session).getBusByName(name);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return bus;
    }

    @Override
    public void approveTrip(Integer tripId) {
        try (SqlSession session = getSession()) {
            try {
                getTripMapper(session).approveTrip(tripId);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void updateTrip(Trip trip) {
        try (SqlSession session = getSession()) {
            try {
                getTripMapper(session).updateTrip(trip);
                trip.getDates().stream().forEach(date -> getDateMapper(session).insertDate(date));
                trip.getDates().stream().forEach(date -> getTripMapper(session).insertDatesToTrip(trip, date, trip.getBus()));
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public Trip getTripById(Integer id) {
        Trip trip = null;
        try (SqlSession session = getSession()) {
            try {
                trip = getTripMapper(session).getTripById(id);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return trip;
    }

    @Override
    public void deleteTripByUserId(Integer id) {
        try (SqlSession session = getSession()) {
            try {
                getTripMapper(session).deleteTripByUserId(id);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public List<Trip> getTripsForAdmin(Parameters parameters) {
        List<Trip> trip = null;
        try (SqlSession session = getSession()) {
            try {
                trip = getTripMapper(session).getTripsForAdmin(parameters);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return trip;
    }

    @Override
    public List<Trip> getTripsForClient(Parameters parameters) {
        List<Trip> trip = null;
        try (SqlSession session = getSession()) {
            try {
                trip = getTripMapper(session).getTripsForClient(parameters);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return trip;
    }

    @Override
    public void deleteDates(Integer tripId) {
        try (SqlSession session = getSession()) {
            try {
                getDateMapper(session).deleteDates(tripId);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void deleteSchedule(Integer tripId) {
        try (SqlSession session = getSession()) {
            try {
                getScheduleMapper(session).deleteSchedule(tripId);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

}
