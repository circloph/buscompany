package net.thumbtack.busserver.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import net.thumbtack.busserver.AppConfig;
import net.thumbtack.busserver.MaintaskApplication;
import net.thumbtack.busserver.daoImpl.DebugDaoImpl;
import net.thumbtack.busserver.dto.request.RegistrationUpdateRequest;
import net.thumbtack.busserver.dto.request.ScheduleRequest;
import net.thumbtack.busserver.dto.request.TripRequest;
import net.thumbtack.busserver.dto.response.BusResponse;
import net.thumbtack.busserver.dto.response.TripResponse;
import net.thumbtack.busserver.dto.response.UserResponse;
import net.thumbtack.busserver.exception.ErrorValidation;

@SpringBootTest(classes = MaintaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TripControllerTest {

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    void dataBaseCleanup() {
        DebugDaoImpl serverDaoImpl = new DebugDaoImpl();
        serverDaoImpl.dataBaseCleanup();
    }
        
    public ResponseEntity<UserResponse> registerAdmin(RegistrationUpdateRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        String userResourceUrl = "http://localhost:8080/api/admins";
        HttpEntity<RegistrationUpdateRequest> requestEntity = new HttpEntity<RegistrationUpdateRequest>(request);
        return restTemplate.exchange(userResourceUrl, HttpMethod.POST, requestEntity, UserResponse.class);
    }

    public ResponseEntity<TripResponse> addTrip(TripRequest tripRequest, String cookie) {
        RestTemplate restTemplate = new RestTemplate();
        String addTripResource = "http://localhost:8080/api/trips";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(tripRequest, requestHeaders);
        return restTemplate.exchange(addTripResource, HttpMethod.POST, requestEntity, TripResponse.class);
    }
 
    public String getCookie(ResponseEntity<UserResponse> responseEntity) {
        HttpHeaders headers = responseEntity.getHeaders();
        return headers.getFirst(HttpHeaders.SET_COOKIE);
    }

    @Test
    void testAddTrip() {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(request);
        String cookie = getCookie(responseAdmin);
        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        HttpEntity<TripResponse> response = addTrip(tripRequest, cookie);

        assertEquals(1, response.getBody().getTripId());
        assertEquals(false, response.getBody().getApproved());
    }


    @Test
    void testAddTrip_BusCompanyException_InvalidScheduleValue() throws JsonMappingException, JsonProcessingException {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(request);
        String cookie = getCookie(responseAdmin);
        
        ScheduleRequest scheduleRequest = new ScheduleRequest("2024-06-03", "2024-06-05", "10");

        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).schedule(scheduleRequest).build();
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> addTrip(tripRequest, cookie));
        String s = exception.getResponseBodyAsString();
        ObjectMapper obj = new ObjectMapper();
        ErrorValidation errors = obj.readValue(s, ErrorValidation.class);
        assertEquals("schedule does not contain days of dispatch", errors.getAllErrors().get(0).getMessage());
    }

    @Test
    void testApproveTrip() {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(request);
        String cookie = getCookie(responseAdmin);
        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        addTrip(tripRequest, cookie);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<TripResponse> responseApproveTrip = restTemplate.exchange("http://localhost:8080/api/trips/1/approve", HttpMethod.PUT, new HttpEntity<>(null, requestHeaders), TripResponse.class);
        assertEquals(1, responseApproveTrip.getBody().getTripId());
        assertEquals(true, responseApproveTrip.getBody().getApproved());
    }

    @Test
    void testDeleteTrip_Success() {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(request);
        String cookie = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();

        addTrip(tripRequest, cookie);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);

        HttpEntity<?> requestEntityForDelete = new HttpEntity<>(null, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/trips/1", HttpMethod.DELETE, requestEntityForDelete, String.class);
        assertEquals("{}", response.getBody());
    }

    @Test
    void testDeleteTrip_BusCompanyException_ImpossibleAction() throws JsonMappingException, JsonProcessingException {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(request);
        String cookie = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);

        addTrip(tripRequest, cookie);

        restTemplate.exchange("http://localhost:8080/api/trips/1/approve", HttpMethod.PUT, new HttpEntity<>(null, requestHeaders), TripResponse.class);


        HttpEntity<?> requestEntityForDelete = new HttpEntity<>(null, requestHeaders);
       
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange("http://localhost:8080/api/trips/1", HttpMethod.DELETE, requestEntityForDelete, String.class));
        String s = exception.getResponseBodyAsString();
        ObjectMapper obj = new ObjectMapper();
        ErrorValidation errors = obj.readValue(s, ErrorValidation.class);
        assertEquals("impossible change/delete the approved trip", errors.getAllErrors().get(0).getMessage());
    
    }

    @Test
    void testGetAllBuses() {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(request);
        String cookie = getCookie(responseAdmin);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        ResponseEntity<List<BusResponse>> buses = restTemplate.exchange("http://localhost:8080/api/buses", HttpMethod.GET, new HttpEntity<>(null, requestHeaders), new ParameterizedTypeReference<List<BusResponse>>() {});
        assertEquals(2, buses.getBody().size());
    }

    @Test
    void testGetTrip() {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(request);
        String cookie = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        addTrip(tripRequest, cookie);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);

        ResponseEntity<TripResponse> trip = restTemplate.exchange("http://localhost:8080/api/trips/1", HttpMethod.GET, new HttpEntity<>(null, requestHeaders), TripResponse.class);

        assertEquals(1, trip.getBody().getTripId());
    }

}
