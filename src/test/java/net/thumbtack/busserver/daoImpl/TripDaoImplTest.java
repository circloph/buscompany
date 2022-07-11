package net.thumbtack.busserver.daoImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Bus;
import net.thumbtack.busserver.model.CustomDate;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Schedule;
import net.thumbtack.busserver.model.Trip;

public class TripDaoImplTest {

    private static TripDaoImpl tripDaoImpl;
    private static UserDaoImpl userDaoImpl;

    @BeforeAll
    static void setUp() {
        tripDaoImpl = new TripDaoImpl();
        userDaoImpl = new UserDaoImpl();

    }

    @BeforeEach
    void dataBaseCleanup() {
        DebugDaoImpl serverDaoImpl = new DebugDaoImpl();
        serverDaoImpl.dataBaseCleanup();
    }

    @Test
    void testApproveTrip() {
    
        List<CustomDate> dates = new ArrayList<>();
        Trip trip = Trip.builder().fromStation("Lenina").toStation("Pushkina")
        .start("12:00").duration("02:00").price(1000).bus(new Bus())
        .schedule(new Schedule()).dates(dates).build();
        tripDaoImpl.insertTrip(trip);
        Trip tripBeforeApproved = tripDaoImpl.getTripById(trip.getId());
        assertTrue(tripBeforeApproved.getApproved() == false);
        tripDaoImpl.approveTrip(trip.getId());
        Trip tripAfterApproved = tripDaoImpl.getTripById(trip.getId());
        assertTrue(tripAfterApproved.getApproved() == true);
    }

    @Test
    void testDeleteTrip() {
        Administration administration = new Administration("Petrov", "Petya", "Petrovich", "Manager", "petrov", "petya1995");
        userDaoImpl.insertAdministration(administration);
        List<CustomDate> dates = new ArrayList<>();
        Trip trip = Trip.builder().userId(administration.getId()).fromStation("Lenina").toStation("Pushkina")
        .start("12:00").duration("02:00").price(1000).bus(new Bus())
        .schedule(new Schedule()).dates(dates).build();
        tripDaoImpl.insertTrip(trip);
        Trip tripBeforeDelete = tripDaoImpl.getTripById(trip.getId());
        assertTrue(tripBeforeDelete != null);
        tripDaoImpl.deleteTripByUserId(trip.getUserId());
        Trip tripAfterDelete = tripDaoImpl.getTripById(trip.getId());
        assertTrue(tripAfterDelete == null);
    }

    @Test
    void testGetAllBuses() {
        List<Bus> buses = tripDaoImpl.getAllBuses();
        assertEquals(2, buses.size());
    }

    @Test
    void testGetBusByName() {
        String busName = "MERCEDES";
        Bus busFromDB = tripDaoImpl.getBusByName(busName);
        assertEquals(busName, busFromDB.getBusName());
    }

    @Test
    void testGetTripById() {
        List<CustomDate> dates = new ArrayList<>();
        Trip trip = Trip.builder().fromStation("Lenina").toStation("Pushkina").start("12:00").duration("02:00").price(1000).bus(new Bus())
        .schedule(new Schedule()).dates(dates).build();
        tripDaoImpl.insertTrip(trip);
        assertEquals(1, trip.getId());
    }

    @Test
    void testGetTripForAdmin() {
        List<CustomDate> dates = new ArrayList<>();
        dates.add(new CustomDate("2024-03-30"));
        Trip firstTrip = Trip.builder().fromStation("Lenina").toStation("Pushkina").start("12:00").duration("02:00").price(1000).bus(new Bus())
        .schedule(new Schedule()).dates(dates).build();
        tripDaoImpl.insertTrip(firstTrip);

        Trip secondTrip = Trip.builder().fromStation("Himikov").toStation("Mira").start("15:00").duration("05:00").price(1500).bus(new Bus())
        .schedule(new Schedule()).dates(dates).build();
        tripDaoImpl.insertTrip(secondTrip);
        tripDaoImpl.approveTrip(firstTrip.getId());

        List<Trip> tripsWithoutParameters = tripDaoImpl.getTripsForAdmin(new Parameters(null, null, null, null, null, null));
        assertEquals(2, tripsWithoutParameters.size());
        List<Trip> tripsWithParameterFromStation = tripDaoImpl.getTripsForAdmin(new Parameters(null, "Himikov", null, null, null, null));
        assertEquals(1, tripsWithParameterFromStation.size());
    }

    @Test
    void testGetTripForClient() {
        List<CustomDate> dates = new ArrayList<>();
        dates.add(new CustomDate("2024-03-30"));
        Trip firstTrip = Trip.builder().fromStation("Lenina").toStation("Pushkina").start("12:00").duration("02:00").price(1000).bus(new Bus())
        .schedule(new Schedule()).dates(dates).build();
        tripDaoImpl.insertTrip(firstTrip);

        Trip secondTrip = Trip.builder().fromStation("Himikov").toStation("Mira").start("15:00").duration("05:00").price(1500).bus(new Bus())
        .schedule(new Schedule()).dates(dates).build();
        tripDaoImpl.insertTrip(secondTrip);
        tripDaoImpl.approveTrip(firstTrip.getId());
        
        List<Trip> tripsWithoutParameters = tripDaoImpl.getTripsForClient(new Parameters(null, null, null, null, null, null));
        assertEquals(1, tripsWithoutParameters.size());
        List<Trip> tripsWithParameterFromStation = tripDaoImpl.getTripsForClient(new Parameters(null, "Himikov", null, null, null, null));
        assertEquals(0, tripsWithParameterFromStation.size());
    }

    @Test
    void testInsertTrip() {
        List<CustomDate> dates = new ArrayList<>();
        dates.add(new CustomDate("2024-03-30"));
        Trip trip = Trip.builder().fromStation("Lenina").toStation("Pushkina").start("12:00").duration("02:00").price(1000).bus(new Bus())
        .schedule(new Schedule()).dates(dates).build();
        tripDaoImpl.insertTrip(trip);
        assertEquals(1, trip.getId());
    }

    @Test
    void testUpdateTrip() {
        List<CustomDate> dates = new ArrayList<>();
        dates.add(new CustomDate("2024-03-30"));
        dates.add(new CustomDate("2024-04-30"));
        dates.add(new CustomDate("2024-05-30"));
        Schedule schedule = new Schedule("2024-03-01", "2024-06-10", "30");
        Trip trip = Trip.builder().fromStation("Lenina").toStation("Pushkina").start("12:00").duration("02:00").price(1000).bus(new Bus())
        .schedule(schedule).dates(dates).build();
        tripDaoImpl.insertTrip(trip);
        String fiedlFromStationBeforeUpdating = trip.getFromStation();
        trip.setFromStation("Molotova");
        tripDaoImpl.updateTrip(trip);
        Trip tripFromDB = tripDaoImpl.getTripById(trip.getId());
        assertNotEquals(fiedlFromStationBeforeUpdating, tripFromDB.getFromStation());
    }
}
