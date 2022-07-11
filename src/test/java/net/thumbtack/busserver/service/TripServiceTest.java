package net.thumbtack.busserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.thumbtack.busserver.MaintaskApplication;
import net.thumbtack.busserver.dao.TripDao;
import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.dto.request.ScheduleRequest;
import net.thumbtack.busserver.dto.request.TripRequest;
import net.thumbtack.busserver.dto.response.BusResponse;
import net.thumbtack.busserver.dto.response.TripResponse;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Bus;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Schedule;
import net.thumbtack.busserver.model.Trip;
import net.thumbtack.busserver.model.User;
import net.thumbtack.busserver.security.SessionProvider;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MaintaskApplication.class)
public class TripServiceTest {

    @MockBean
    private TripDao tripDao;
    
    @MockBean
    private UserDao userDao;

    @MockBean
    private SessionProvider sessionProvider;

    @Autowired
    private TripService tripService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testAddTripWithSchedule() throws ParseException, BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");

        ScheduleRequest scheduleRequest = new ScheduleRequest("2024-03-01", "2024-06-10", "30");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina")
        .toStation("Himikov").start("16:00").duration("02:00")
        .price(100).schedule(scheduleRequest).build();

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((Trip)args[0]).setId(1);
            return null;
        }).when(tripDao).insertTrip(Mockito.any(Trip.class));

        TripResponse tripResponse = tripService.addTrip(tripRequest, cookie, mock(HttpServletResponse.class));

        assertEquals(1, tripResponse.getTripId());
        assertEquals(3, tripResponse.getDates().size());
    }

    @Test
    void testAddTripWithDates() throws ParseException, BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");

        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina")
        .toStation("Himikov").start("16:00").duration("02:00")
        .price(100).dates(dates).build();

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((Trip)args[0]).setId(1);
            return null;
        }).when(tripDao).insertTrip(Mockito.any(Trip.class));

        TripResponse tripResponse = tripService.addTrip(tripRequest, cookie, mock(HttpServletResponse.class));

        assertEquals(1, tripResponse.getTripId());
        assertEquals(1, tripResponse.getDates().size());
        assertTrue(tripResponse.getSchedule() == null);
    }

    @Test
    void testApprovedTrip() {
        Cookie cookie = mock(Cookie.class);
        Trip trip = new Trip(1, 1, "Lenina", "Himikov", "16;00", "02:00", 1000, new Bus(), true, new Schedule(), new ArrayList<>());
        when(tripDao.getTripById(Mockito.anyInt())).thenReturn(trip);
        TripResponse tripResponse = tripService.approveTrip(1, cookie, mock(HttpServletResponse.class));
        verify(tripDao, times(1)).approveTrip(1);
        assertEquals(true, tripResponse.getApproved());
    }

    @Test
    void testDeleteTrip() throws BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        Trip trip = new Trip(1, 1, "Lenina", "Himikov", "16;00", "02:00", 1000, new Bus(), false, new Schedule(), new ArrayList<>());
        when(tripDao.getTripById(Mockito.anyInt())).thenReturn(trip);

        tripService.deleteTrip(1, cookie, mock(HttpServletResponse.class));
        verify(tripDao, times(1)).deleteTripByUserId(1);
    }

    @Test
    void testDeleteTrip_BusCompanyException_DeleteApprovedTrip() throws BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        Trip trip = new Trip(1, 1, "Lenina", "Himikov", "16;00", "02:00", 1000, new Bus(), true, new Schedule(), new ArrayList<>());
        when(tripDao.getTripById(Mockito.anyInt())).thenReturn(trip);

        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> tripService.deleteTrip(1, cookie, mock(HttpServletResponse.class)));
        assertEquals("impossible change/delete the approved trip", exception.getError().getMessage());
    }

    @Test
    void testGetAllBuses() {
        Cookie cookie = mock(Cookie.class);
        List<Bus> buses = new ArrayList<Bus>();
        buses.add(new Bus(1, "MERCEDES", 20));
        when(tripDao.getAllBuses()).thenReturn(buses);
        List<BusResponse> busResponses = tripService.getAllBuses(cookie, mock(HttpServletResponse.class));
        verify(tripDao, times(1)).getAllBuses();
        assertEquals(buses.get(0).getBusName(), busResponses.get(0).getBusName());

    }

    @Test
    void testGetTrip() {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        Trip trip = new Trip(1,1, "Lenina", "Himikov", "16;00", "02:00", 1000, new Bus(), true, new Schedule(), new ArrayList<>());
        when(tripDao.getTripById(1)).thenReturn(trip);

        TripResponse tripResponse = tripService.getTrip(1, cookie, mock(HttpServletResponse.class));
        assertEquals(trip.getFromStation(), tripResponse.getFromStation());
        assertEquals(trip.getToStation(), tripResponse.getToStation());
        assertEquals(trip.getStart(), tripResponse.getStart());
    }

    @Test
    void testGetTrips() {
        User admin = new Administration("Ivanov", "Ivan", "Ivanovich", "director", "ivanov", passwordEncoder.encode("ivanivan"));
        List<Trip> tripsForClient = new ArrayList<>();
        tripsForClient.add(new Trip(1, 1,"Lenina", "Himikov", "16:00", "02:00", 1000, new Bus(), true, new Schedule(), new ArrayList<>()));
        
        
        List<Trip> tripsForAdmin = new ArrayList<>();
        tripsForAdmin.add(new Trip(1, 1, "Lenina", "Himikov", "16:00", "02:00", 1000, new Bus(), true, new Schedule(), new ArrayList<>()));
        tripsForAdmin.add(new Trip(2, 1, "Himikov", "Lenina", "10:00", "01:20", 2300, new Bus(), false, new Schedule(), new ArrayList<>()));

        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(admin);
        when(userDao.getUserByLogin("ivanov")).thenReturn(admin);
        Parameters parameters = new Parameters(null, null, null, null, "2024-06-10");
        when(tripDao.getTripsForClient(parameters)).thenReturn(tripsForClient);
        when(tripDao.getTripsForAdmin(parameters)).thenReturn(tripsForAdmin);

        List<TripResponse> tripResponses = tripService.getTrips(parameters, cookie, mock(HttpServletResponse.class));
        assertEquals(2, tripResponses.size());
    }

    @Test
    void testUpdateTrip() throws ParseException, BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina")
        .toStation("Himikov").start("16:00").duration("02:00")
        .price(100).dates(dates).build();

        Trip trip = new Trip(1, 1, "Lenina", "Himikov", "16;00", "02:00", 1000, new Bus(), false, new Schedule(), new ArrayList<>());
        when(tripDao.getTripById(1)).thenReturn(trip);

        TripResponse tripResponse = tripService.updateTrip(1, tripRequest, cookie, mock(HttpServletResponse.class));
        verify(tripDao, times(1)).deleteSchedule(Mockito.anyInt());
        verify(tripDao, times(1)).deleteDates(Mockito.anyInt());
        assertEquals(1, tripResponse.getDates().size());
    }

    @Test
    void testUpdateTrip_BusCompanyException_approvedTrue() throws ParseException, BusCompanyException {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina")
        .toStation("Himikov").start("16:00").duration("02:00")
        .price(100).dates(dates).build();

        Trip trip = new Trip(1, 1, "Lenina", "Himikov", "16;00", "02:00", 1000, new Bus(), true, new Schedule(), new ArrayList<>());
        when(tripDao.getTripById(1)).thenReturn(trip);

        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> tripService.updateTrip(1, tripRequest, cookie, mock(HttpServletResponse.class)));
        assertEquals("impossible change/delete the approved trip", exception.getError().getMessage());
    }
}
