package net.thumbtack.busserver.service;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.thumbtack.busserver.dao.TripDao;
import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.dto.mappers.TripDtoMapper;
import net.thumbtack.busserver.dto.request.TripRequest;
import net.thumbtack.busserver.dto.response.BusResponse;
import net.thumbtack.busserver.dto.response.TripResponse;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.CustomDate;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Schedule;
import net.thumbtack.busserver.model.Session;
import net.thumbtack.busserver.model.Trip;
import net.thumbtack.busserver.model.User;
import net.thumbtack.busserver.security.SessionProvider;

@Service
public class TripService {

    private static final Map<String, String> convertReceivedDayOfWeek = new HashMap<>();
    
    private TripDao tripDao;

    private UserDao userDao;

    @Value("${user_idle_timeout}")
    private int userIdleTimeout;

    private SessionProvider sessionProvider;

    private TripDtoMapper tripMapper;

    static {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        String[] keysShortWeekDays = dateFormatSymbols.getShortWeekdays();
        String[] valuesWeekDays = dateFormatSymbols.getWeekdays();
        for (int i = 0; i < keysShortWeekDays.length; i++) {
            convertReceivedDayOfWeek.put(keysShortWeekDays[i], valuesWeekDays[i].toUpperCase());
        }
     }

    @Autowired
    public TripService(TripDao tripDao, SessionProvider sessionProvider, UserDao userDao) {
        this.tripDao = tripDao;
        this.sessionProvider = sessionProvider;
        this.userDao = userDao;
        this.tripMapper = Mappers.getMapper(TripDtoMapper.class);

    }

    public List<BusResponse> getAllBuses(Cookie cookie, HttpServletResponse response) {
        String sessionId = cookie.getValue();
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);
        return tripMapper.listBusToListResponse(tripDao.getAllBuses());
    }

    public TripResponse addTrip(TripRequest tripRequest, Cookie cookie, HttpServletResponse response) throws ParseException, BusCompanyException {
        String sessionId = cookie.getValue();
        Integer userId = userDao.getUserIdBySessionId(sessionId);
        List<CustomDate> dates = null;
        Trip trip = tripMapper.tripRequestToTrip(tripRequest);
        if (tripRequest.getSchedule() != null) {
            dates = calculateDates(trip.getSchedule());
            if (dates.isEmpty()) {
                throw new BusCompanyException(ErrorCode.INVALID_SCHEDULE_VALUE);
            }
            trip.setDates(dates);
        }
        trip.setApproved(false);
        trip.setBus(tripDao.getBusByName(tripRequest.getBusName()));
        trip.setUserId(userId);
        tripDao.insertTrip(trip);
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);
        return tripMapper.tripToTripResponse(trip);
    }

    private List<CustomDate> calculateDates(Schedule schedule) throws ParseException {
        LocalDate startDate = LocalDate.parse(schedule.getFromDate());
        LocalDate endDate = LocalDate.parse(schedule.getToDate());
        if (schedule.getPeriod().equals("daily")) {
            return startDate.datesUntil(endDate.plusDays(1)).map(date -> new CustomDate(date.toString())).collect(Collectors.toList());
        }
        if (schedule.getPeriod().equals("even")) {
            return startDate.datesUntil(endDate.plusDays(1)).filter(local -> local.getDayOfMonth() % 2 == 0).map(date -> new CustomDate(date.toString())).collect(Collectors.toList());
        }
        if (schedule.getPeriod().equals("odd")) {
            return startDate.datesUntil(endDate.plusDays(1)).filter(local -> local.getDayOfMonth() % 2 != 0).map(date -> new CustomDate(date.toString())).collect(Collectors.toList());
        }
        if (schedule.getPeriod().matches("^[a-zA-Z,\\s]+$")) {
            List<DayOfWeek> receivedDaysOfWeekFromRequest = Arrays.stream(schedule.getPeriod().split(",")).map(day -> DayOfWeek.valueOf(convertReceivedDayOfWeek.get(day))).collect(Collectors.toList());
            return startDate.datesUntil(endDate.plusDays(1)).filter(date -> receivedDaysOfWeekFromRequest.contains(date.getDayOfWeek())).map(date -> new CustomDate(date.toString())).collect(Collectors.toList());
        }
        List<Integer> receivedDaysFromRequest = Arrays.stream(schedule.getPeriod().split(", ")).map(s -> Integer.valueOf(s)).collect(Collectors.toList());
        return startDate.datesUntil(endDate.plusDays(1)).filter(date -> receivedDaysFromRequest.contains(date.getDayOfMonth())).map(date -> new CustomDate(date.toString())).collect(Collectors.toList());
    }


    public TripResponse approveTrip(Integer tripId, Cookie cookie, HttpServletResponse response) {
        String sessionId = cookie.getValue();
        tripDao.approveTrip(tripId); 
        Trip trip = tripDao.getTripById(tripId);
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);
        return tripMapper.tripToTripResponse(trip);
    }

    public TripResponse updateTrip(Integer id, TripRequest tripRequest, Cookie cookie, HttpServletResponse response) throws ParseException, BusCompanyException {
        String sessionId = cookie.getValue();
        List<CustomDate> dates = null;
        Trip trip = tripDao.getTripById(id);
        if (trip.getApproved() != null && !trip.getApproved()) {
            tripMapper.updateTripFromRequest(trip, tripRequest);
            tripDao.deleteDates(trip.getId());
            if (tripRequest.getSchedule() != null) {
                dates = calculateDates(trip.getSchedule());
                trip.setDates(dates);
            } else if (!tripRequest.getDates().isEmpty()) {
                tripDao.deleteSchedule(trip.getId());
            }
            tripDao.updateTrip(trip);
        } else {
            userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
            sessionProvider.extendExpirationCookie(cookie, response);
            throw new BusCompanyException(ErrorCode.IMPOSSIBLE_ACTION);    
        }
        
        return tripMapper.tripToTripResponse(trip);
    }

    public String deleteTrip(Integer tripId, Cookie cookie, HttpServletResponse response) throws BusCompanyException {
        String sessionId = cookie.getValue();
        Trip trip = tripDao.getTripById(tripId);
        if (trip.getApproved() != null && !trip.getApproved()) {
            tripDao.deleteTripByUserId(tripId);
            userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
            sessionProvider.extendExpirationCookie(cookie, response);
            return "{}";
        }
        throw new BusCompanyException(ErrorCode.IMPOSSIBLE_ACTION);    
    }

    public TripResponse getTrip(Integer tripId, Cookie cookie, HttpServletResponse response) {
        String sessionId = cookie.getValue();
        Trip trip = tripDao.getTripById(tripId);
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);

        return tripMapper.tripToTripResponse(trip);
    }

    public List<TripResponse> getTrips(Parameters parameters, Cookie cookie, HttpServletResponse response) {
        List<Trip> trips = null;
        String sessionId = cookie.getValue();
        User user = userDao.getUserBySessionId(sessionId);
        if (user instanceof Client) {
            trips = tripDao.getTripsForClient(parameters);
        } else {
            trips = tripDao.getTripsForAdmin(parameters);
        }
        Set<Trip> set = new LinkedHashSet<>(trips);
        trips.clear();
        trips.addAll(set);
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);
        return tripMapper.tripToTripResponse(trips);
    }

}
