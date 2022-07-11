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
import net.thumbtack.busserver.dto.request.OrderRequest;
import net.thumbtack.busserver.dto.request.PassengerRequest;
import net.thumbtack.busserver.dto.request.PlaceRequest;
import net.thumbtack.busserver.dto.request.RegistrationUpdateRequest;
import net.thumbtack.busserver.dto.request.TripRequest;
import net.thumbtack.busserver.dto.response.OrderResponse;
import net.thumbtack.busserver.dto.response.PlaceResponse;
import net.thumbtack.busserver.dto.response.TripResponse;
import net.thumbtack.busserver.dto.response.UserResponse;
import net.thumbtack.busserver.exception.ErrorValidation;

@SpringBootTest(classes = MaintaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderControllerTest {

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    void dataBaseCleanup() {
        DebugDaoImpl serverDaoImpl = new DebugDaoImpl();
        serverDaoImpl.dataBaseCleanup();
    }

    public ResponseEntity<UserResponse> registerAdmin(RegistrationUpdateRequest request) {
        String userResourceUrl = "http://localhost:8080/api/admins";
        HttpEntity<RegistrationUpdateRequest> requestEntity = new HttpEntity<RegistrationUpdateRequest>(request);
        return restTemplate.exchange(userResourceUrl, HttpMethod.POST, requestEntity, UserResponse.class);
    }

    public ResponseEntity<UserResponse> registerClient(RegistrationUpdateRequest request) {
        String userResourceUrl = "http://localhost:8080/api/clients";
        HttpEntity<RegistrationUpdateRequest> requestEntity = new HttpEntity<RegistrationUpdateRequest>(request);
        return restTemplate.exchange(userResourceUrl, HttpMethod.POST, requestEntity, UserResponse.class);
    }

    public ResponseEntity<TripResponse> addTrip(TripRequest tripRequest, String cookie) {
        String addTripResource = "http://localhost:8080/api/trips";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(tripRequest, requestHeaders);
        return restTemplate.exchange(addTripResource, HttpMethod.POST, requestEntity, TripResponse.class);
    }

    public ResponseEntity<TripResponse> approveTrip(String cookie) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        return restTemplate.exchange("http://localhost:8080/api/trips/1/approve", HttpMethod.PUT, new HttpEntity<>(null, requestHeaders), TripResponse.class);
    }

    public ResponseEntity<OrderResponse> orderingTicket(OrderRequest orderRequest, String cookie) {
        String orderingTicketResource = "http://localhost:8080/api/orders";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(orderRequest, requestHeaders);
        return restTemplate.exchange(orderingTicketResource, HttpMethod.POST, requestEntity, OrderResponse.class);
    }

    public ResponseEntity<String> logoutUser(String cookie) {
        RestTemplate restTemplate = new RestTemplate();
        String userResourceUrl = "http://localhost:8080/api/sessions";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
        return restTemplate.exchange(userResourceUrl, HttpMethod.DELETE, requestEntity, String.class);
    }

    public String getCookie(ResponseEntity<UserResponse> responseEntity) {
        HttpHeaders headers = responseEntity.getHeaders();
        return headers.getFirst(HttpHeaders.SET_COOKIE);
    }

    public ResponseEntity<PlaceResponse> selectPlace(PlaceRequest placeRequest, String cookie) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        String selectPlaceResource = "http://localhost:8080/api/places";
        return restTemplate.exchange(selectPlaceResource, HttpMethod.POST, new HttpEntity<>(placeRequest, requestHeaders), PlaceResponse.class);

    }

    @Test
    void testCancelOrder() {
        RegistrationUpdateRequest requestAdmin = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin1").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(requestAdmin);
        String cookieAdmin = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        addTrip(tripRequest, cookieAdmin);
        approveTrip(cookieAdmin);
        logoutUser(cookieAdmin);

        RegistrationUpdateRequest requestClient = RegistrationUpdateRequest.builder().lastname("Мансов").firstname("Георгий").patronymic("Вячеславович").numberPhone("88005553535").email("ivana@gmai.com").login("client").password("clientclient").build();
        ResponseEntity<UserResponse> responseClient = registerClient(requestClient);
        String cookieClient = getCookie(responseClient);

        List<PassengerRequest> passengers = new ArrayList<>();
        passengers.add(PassengerRequest.builder().firstName("Bogdan").lastName("Zhukov").passport("666666").build());
        OrderRequest orderRequest = new OrderRequest(1, "2024-06-03", passengers);

        orderingTicket(orderRequest, cookieClient);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookieClient);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
        ResponseEntity<String> result = restTemplate.exchange("http://localhost:8080/api/orders/1", HttpMethod.DELETE, requestEntity, String.class);
        
        assertEquals("{}", result.getBody());
    }

    @Test
    void testGetFreePlaces() {
        RegistrationUpdateRequest requestAdmin = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(requestAdmin);
        String cookieAdmin = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        addTrip(tripRequest, cookieAdmin);
        approveTrip(cookieAdmin);
        logoutUser(cookieAdmin);

        RegistrationUpdateRequest requestClient = RegistrationUpdateRequest.builder().lastname("Мансов").firstname("Георгий").patronymic("Вячеславович").numberPhone("88005553535").email("ivana@gmai.com").login("client").password("clientclient").build();
        ResponseEntity<UserResponse> responseClient = registerClient(requestClient);
        String cookieClient = getCookie(responseClient);

        List<PassengerRequest> passengers = new ArrayList<>();
        passengers.add(PassengerRequest.builder().firstName("Bogdan").lastName("Zhukov").passport("666666").build());
        OrderRequest orderRequest = new OrderRequest(1, "2024-06-03", passengers);

        orderingTicket(orderRequest, cookieClient);

        PlaceRequest placeRequest = new PlaceRequest(1, "Zhukov", "Bogdan", "666666", "1");

        selectPlace(placeRequest, cookieClient);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookieClient);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
        ResponseEntity<List<Integer>> freePlaces = restTemplate.exchange("http://localhost:8080/api/places/1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<Integer>>(){});
        
        assertEquals(23, freePlaces.getBody().size());
    }

    @Test
    void testGetOrdersList() {
        RegistrationUpdateRequest requestAdmin = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(requestAdmin);
        String cookieAdmin = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        addTrip(tripRequest, cookieAdmin);
        approveTrip(cookieAdmin);
        logoutUser(cookieAdmin);

        RegistrationUpdateRequest requestClient = RegistrationUpdateRequest.builder().lastname("Мансов").firstname("Георгий").patronymic("Вячеславович").numberPhone("88005553535").email("ivana@gmai.com").login("client").password("clientclient").build();
        ResponseEntity<UserResponse> responseClient = registerClient(requestClient);
        String cookieClient = getCookie(responseClient);

        List<PassengerRequest> firstPassengers = new ArrayList<>();
        firstPassengers.add(PassengerRequest.builder().firstName("Bogdan").lastName("Zhukov").passport("666666").build());
        OrderRequest firstOrder = new OrderRequest(1, "2024-06-03", firstPassengers);

        List<PassengerRequest> secondPassengers = new ArrayList<>();
        secondPassengers.add(PassengerRequest.builder().firstName("Vasiliy").lastName("Mamov").passport("777777").build());
        OrderRequest secondOrder = new OrderRequest(1, "2024-06-03", secondPassengers);

        orderingTicket(firstOrder, cookieClient);
        orderingTicket(secondOrder, cookieClient);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookieClient);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
        ResponseEntity<List<OrderResponse>> result = restTemplate.exchange("http://localhost:8080/api/orders", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<OrderResponse>>() {});
        
        assertEquals(2, result.getBody().size());

    }

    @Test
    void testOrderingTicket() {
        RegistrationUpdateRequest requestAdmin = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(requestAdmin);
        String cookieAdmin = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        addTrip(tripRequest, cookieAdmin);
        approveTrip(cookieAdmin);
        logoutUser(cookieAdmin);

        RegistrationUpdateRequest requestClient = RegistrationUpdateRequest.builder().lastname("Мансов").firstname("Георгий").patronymic("Вячеславович").numberPhone("88005553535").email("ivana@gmai.com").login("client").password("clientclient").build();
        ResponseEntity<UserResponse> responseClient = registerClient(requestClient);
        String cookieClient = getCookie(responseClient);

        List<PassengerRequest> passengers = new ArrayList<>();
        passengers.add(PassengerRequest.builder().firstName("Bogdan").lastName("Zhukov").passport("666666").build());
        OrderRequest orderRequest = new OrderRequest(1, "2024-06-03", passengers);

        ResponseEntity<OrderResponse> order = orderingTicket(orderRequest, cookieClient);

        assertEquals(1, order.getBody().getPassengers().size());
        assertEquals(1, order.getBody().getOrderId());
    }

    @Test
    void testOrderingTicket_BusCompanyException_InvalidDate() throws JsonMappingException, JsonProcessingException {
        RegistrationUpdateRequest requestAdmin = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(requestAdmin);
        String cookieAdmin = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        addTrip(tripRequest, cookieAdmin);
        approveTrip(cookieAdmin);
        logoutUser(cookieAdmin);

        RegistrationUpdateRequest requestClient = RegistrationUpdateRequest.builder().lastname("Мансов").firstname("Георгий").patronymic("Вячеславович").numberPhone("88005553535").email("ivana@gmai.com").login("client").password("clientclient").build();
        ResponseEntity<UserResponse> responseClient = registerClient(requestClient);
        String cookieClient = getCookie(responseClient);

        List<PassengerRequest> passengers = new ArrayList<>();
        passengers.add(PassengerRequest.builder().firstName("Bogdan").lastName("Zhukov").passport("666666").build());
        OrderRequest orderRequest = new OrderRequest(1, "2028-06-03", passengers);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,  () -> orderingTicket(orderRequest, cookieClient));

        ObjectMapper obj = new ObjectMapper();
        String s = exception.getResponseBodyAsString();
        ErrorValidation error = obj.readValue(s, ErrorValidation.class);

        assertEquals("there is no such date on the trip", error.getAllErrors().get(0).getMessage());
    }


    @Test
    void testPlaceSelect() {
        RegistrationUpdateRequest requestAdmin = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(requestAdmin);
        String cookieAdmin = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        addTrip(tripRequest, cookieAdmin);
        approveTrip(cookieAdmin);
        logoutUser(cookieAdmin);

        RegistrationUpdateRequest requestClient = RegistrationUpdateRequest.builder().lastname("Мансов").firstname("Георгий").patronymic("Вячеславович").numberPhone("88005553535").email("ivana@gmai.com").login("client").password("clientclient").build();
        ResponseEntity<UserResponse> responseClient = registerClient(requestClient);
        String cookieClient = getCookie(responseClient);

        List<PassengerRequest> passengers = new ArrayList<>();
        passengers.add(PassengerRequest.builder().firstName("Bogdan").lastName("Zhukov").passport("666666").build());
        OrderRequest orderRequest = new OrderRequest(1, "2024-06-03", passengers);

        orderingTicket(orderRequest, cookieClient);

        PlaceRequest placeRequest = new PlaceRequest(1, "Zhukov", "Bogdan", "666666", "1");

        ResponseEntity<PlaceResponse> place = selectPlace(placeRequest, cookieClient);

        assertEquals("Билет 1_1", place.getBody().getTicket());
    }

    @Test
    void testPlaceSelect_BusCompanyException_InvalidPassengerData() throws JsonMappingException, JsonProcessingException {
        RegistrationUpdateRequest requestAdmin = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(requestAdmin);
        String cookieAdmin = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest tripRequest = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();
        
        addTrip(tripRequest, cookieAdmin);
        approveTrip(cookieAdmin);
        logoutUser(cookieAdmin);

        RegistrationUpdateRequest requestClient = RegistrationUpdateRequest.builder().lastname("Мансов").firstname("Георгий").patronymic("Вячеславович").numberPhone("88005553535").email("ivana@gmai.com").login("client").password("clientclient").build();
        ResponseEntity<UserResponse> responseClient = registerClient(requestClient);
        String cookieClient = getCookie(responseClient);

        List<PassengerRequest> passengers = new ArrayList<>();
        passengers.add(PassengerRequest.builder().firstName("Bogdan").lastName("Zhukov").passport("666666").build());
        OrderRequest orderRequest = new OrderRequest(1, "2024-06-03", passengers);

        orderingTicket(orderRequest, cookieClient);

        PlaceRequest placeRequest = new PlaceRequest(1, "wrongLastName", "Bogdan", "666666", "1");

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,  () -> selectPlace(placeRequest, cookieClient));
        String s = exception.getResponseBodyAsString();
        ObjectMapper obj = new ObjectMapper();
        ErrorValidation error = obj.readValue(s, ErrorValidation.class);

        assertEquals("there is no such passenger in the order", error.getAllErrors().get(0).getMessage());
    }
}