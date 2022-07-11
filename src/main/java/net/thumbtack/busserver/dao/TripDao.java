package net.thumbtack.busserver.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import net.thumbtack.busserver.model.Bus;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Trip;

@Component
public interface TripDao {

    List<Bus> getAllBuses();

    void insertTrip(Trip trip);

    Bus getBusByName(String name);

    void approveTrip(Integer tripId);

    void updateTrip(Trip trip);

    Trip getTripById(Integer id);

    void deleteTripByUserId(Integer userId);

    void deleteDates(Integer tripId);
    
    void deleteSchedule(Integer tripId);

    List<Trip> getTripsForAdmin(Parameters parameters);
    
    List<Trip> getTripsForClient(Parameters parameters);

}
