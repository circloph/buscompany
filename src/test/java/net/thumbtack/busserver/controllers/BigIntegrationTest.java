package net.thumbtack.busserver.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import net.thumbtack.busserver.AppConfig;
import net.thumbtack.busserver.MaintaskApplication;
import net.thumbtack.busserver.daoImpl.DebugDaoImpl;
import net.thumbtack.busserver.dto.request.AuthenticationRequest;
import net.thumbtack.busserver.dto.request.OrderRequest;
import net.thumbtack.busserver.dto.request.PassengerRequest;
import net.thumbtack.busserver.dto.request.PlaceRequest;
import net.thumbtack.busserver.dto.request.RegistrationUpdateRequest;
import net.thumbtack.busserver.dto.request.ScheduleRequest;
import net.thumbtack.busserver.dto.request.TripRequest;
import net.thumbtack.busserver.dto.response.OrderResponse;
import net.thumbtack.busserver.dto.response.PlaceResponse;
import net.thumbtack.busserver.dto.response.TripResponse;
import net.thumbtack.busserver.dto.response.UserResponse;
import net.thumbtack.busserver.model.Parameters;

@SpringBootTest(classes = MaintaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BigIntegrationTest {

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

    public ResponseEntity<TripResponse> approveTrip(String cookie, Integer tripId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        return restTemplate.exchange("http://localhost:8080/api/trips/" + tripId + "/approve", HttpMethod.PUT, new HttpEntity<>(null, requestHeaders), TripResponse.class);
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

    public ResponseEntity<List<TripResponse>> getTrips(String cookie, Parameters params) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        String getTripResource = "http://localhost:8080/api/trips";
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(getTripResource)
        .queryParamIfPresent("busName", Optional.ofNullable(params.getBusName()))
        .queryParamIfPresent("fromDate", Optional.ofNullable(params.getFromDate()))
        .queryParamIfPresent("toDate", Optional.ofNullable(params.getToDate()))
        .queryParamIfPresent("fromStation", Optional.ofNullable(params.getFromStation()))
        .queryParamIfPresent("toStation", Optional.ofNullable(params.getToStation()))
        .encode()
        .toUriString();
        return restTemplate.exchange(urlTemplate, HttpMethod.GET, new HttpEntity<>(null, requestHeaders), new ParameterizedTypeReference<List<TripResponse>>(){}, params);
    }

    public ResponseEntity<List<Integer>> getFreePlaces(String cookie, Integer orderId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
        return restTemplate.exchange("http://localhost:8080/api/places/" + orderId, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<Integer>>(){});
    }

    public ResponseEntity<List<OrderResponse>> getOrdersList(String cookie, Parameters params) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        String getTripResource = "http://localhost:8080/api/orders";
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(getTripResource)
        .queryParamIfPresent("busName", Optional.ofNullable(params.getBusName()))
        .queryParamIfPresent("fromDate", Optional.ofNullable(params.getFromDate()))
        .queryParamIfPresent("toDate", Optional.ofNullable(params.getToDate()))
        .queryParamIfPresent("fromStation", Optional.ofNullable(params.getFromStation()))
        .queryParamIfPresent("toStation", Optional.ofNullable(params.getToStation()))
        .queryParamIfPresent("clientId", Optional.ofNullable(params.getUserId()))
        .encode()
        .toUriString();
        return restTemplate.exchange(urlTemplate, HttpMethod.GET, new HttpEntity<>(null, requestHeaders), new ParameterizedTypeReference<List<OrderResponse>>(){}, params);
    }

    public ResponseEntity<UserResponse> updateClient(String cookie, RegistrationUpdateRequest updateClient) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(updateClient, requestHeaders);
        return restTemplate.exchange("http://localhost:8080/api/clients", HttpMethod.PUT, requestEntity, UserResponse.class);
    }

    public ResponseEntity<String> cancelOrder(String cookie, Integer orderId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
        return restTemplate.exchange("http://localhost:8080/api/orders/" + orderId, HttpMethod.DELETE, requestEntity, String.class);
    }

    public ResponseEntity<UserResponse> loginUser(AuthenticationRequest authenticationRequest) {
        HttpEntity<?> requestEntity = new HttpEntity<>(authenticationRequest, null);
        return restTemplate.exchange("http://localhost:8080/api/sessions", HttpMethod.POST, requestEntity, UserResponse.class);
    }
 
    @Test
    void fullProccess() {
        RegistrationUpdateRequest requestAdmin = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> responseAdmin = registerAdmin(requestAdmin);
        String cookieAdmin = getCookie(responseAdmin);

        List<String> dates = new ArrayList<String>();
        dates.add("2024-06-03");
        TripRequest firstTripWithDates = TripRequest.builder().busName("MERCEDES").fromStation("Lenina").toStation("Himikov")
        .start("16:00").duration("02:00").price(1000).dates(dates).build();

        ScheduleRequest firstSchedule = new ScheduleRequest("2022-01-20", "2022-02-03", "5, 10, 15, 25, 30");
        TripRequest secondTripWithSchedule = TripRequest.builder().busName("SCANIA").fromStation("Mira").toStation("Pushkina")
        .start("12:00").duration("03:00").price(2000).schedule(firstSchedule).build();
        
        ScheduleRequest secondSchedule = new ScheduleRequest("2022-01-20", "2022-02-03", "5, 10, 15, 25, 30");
        TripRequest thirdTripWithSchedule = TripRequest.builder().busName("SCANIA").fromStation("Mira").toStation("Pushkina")
        .start("13:00").duration("06:00").price(2500).schedule(secondSchedule).build();
        
        ResponseEntity<TripResponse> firstTripResponse = addTrip(firstTripWithDates, cookieAdmin);
        ResponseEntity<TripResponse> secondTripResponse = addTrip(secondTripWithSchedule, cookieAdmin);
        ResponseEntity<TripResponse> thirdTripResponse = addTrip(thirdTripWithSchedule, cookieAdmin);
        approveTrip(cookieAdmin, firstTripResponse.getBody().getTripId());
        approveTrip(cookieAdmin, secondTripResponse.getBody().getTripId());
        approveTrip(cookieAdmin, thirdTripResponse.getBody().getTripId());
        logoutUser(cookieAdmin);

        RegistrationUpdateRequest firstClient = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").numberPhone("89005842323").email("ekeorg@gmai.com").login("arkadiev").password("ivan1000").build();
        ResponseEntity<UserResponse> firstResponse = registerClient(firstClient);

        String firstCookieClient = getCookie(firstResponse);
        Parameters params = new Parameters(null, null, "SCANIA", null, null);
        
        ResponseEntity<List<TripResponse>> responseTrips = getTrips(firstCookieClient, params);
        assertEquals(2, responseTrips.getBody().size());

        Integer firstTripId = responseTrips.getBody().get(0).getTripId();

        List<PassengerRequest> firstPassengers = new ArrayList<>();
        firstPassengers.add(PassengerRequest.builder().firstName("Bogdan").lastName("Zhukov").passport("666666").build());
        OrderRequest orderRequest = new OrderRequest(firstTripId, "2022-01-25", firstPassengers);
        ResponseEntity<OrderResponse> firstOrderResponse = orderingTicket(orderRequest, firstCookieClient);
        Integer firstOrderId = firstOrderResponse.getBody().getOrderId();
        assertEquals(1, firstOrderResponse.getBody().getOrderId());

        PlaceRequest placeRequest = new PlaceRequest(1, "Zhukov", "Bogdan", "666666", "1");
        ResponseEntity<PlaceResponse> firstPlaceResponse = selectPlace(placeRequest, firstCookieClient);
        assertEquals(1, firstPlaceResponse.getBody().getOrderId());
        assertEquals("Билет " + firstTripId + "_" + placeRequest.getPlace(), firstPlaceResponse.getBody().getTicket());
        
        ResponseEntity<List<Integer>> firstFreePlacesList =  getFreePlaces(firstCookieClient, firstOrderId);
        assertEquals(19, firstFreePlacesList.getBody().size());
        logoutUser(firstCookieClient);

        RegistrationUpdateRequest secondClient = RegistrationUpdateRequest.builder().lastname("Пупкин").firstname("Александр").patronymic("Григорьевич").numberPhone("89015841123").email("grigorie@gmai.com").login("mamole").password("alexander123").build();
        ResponseEntity<UserResponse> secondResponse = registerClient(secondClient);
        String secondCookieClient = getCookie(secondResponse);
        Parameters secondParams = new Parameters(null, null, "SCANIA", null, null);
        ResponseEntity<List<TripResponse>> secondResponseTrips = getTrips(secondCookieClient, secondParams);
        assertEquals(2, secondResponseTrips.getBody().size());
        Integer secondTripId = secondResponseTrips.getBody().get(0).getTripId();

        List<PassengerRequest> secondPassengers1 = new ArrayList<>();
        List<PassengerRequest> secondPassengers2 = new ArrayList<>();
        secondPassengers1.add(PassengerRequest.builder().firstName("Vasya").lastName("Kotov").passport("777777").build());
        secondPassengers1.add(PassengerRequest.builder().firstName("Kostya").lastName("Zaharov").passport("123456").build());
        secondPassengers2.add(PassengerRequest.builder().firstName("Evgeniy").lastName("Zuev").passport("654321").build());
        secondPassengers2.add(PassengerRequest.builder().firstName("Stas").lastName("Molotov").passport("112233").build());
        OrderRequest secondOrderRequest1 = new OrderRequest(secondTripId, "2022-01-25", secondPassengers1);
        OrderRequest secondOrderRequest2 = new OrderRequest(secondTripId, "2022-01-25", secondPassengers2);
        
        ResponseEntity<OrderResponse> secondOrderResponse1 = orderingTicket(secondOrderRequest1, secondCookieClient);
        Integer secondOrderId1 = secondOrderResponse1.getBody().getOrderId();
        assertEquals(2, secondOrderId1);

        ResponseEntity<OrderResponse> secondOrderResponse2 = orderingTicket(secondOrderRequest2, secondCookieClient);
        Integer secondOrderId2 = secondOrderResponse2.getBody().getOrderId();
        assertEquals(3, secondOrderId2);

        Parameters parameters = new Parameters(null, null, null, null, "2022-01-01", "2022-01-30");
        ResponseEntity<List<OrderResponse>> secondOrderResponse = getOrdersList(secondCookieClient, parameters);
        assertEquals(2, secondOrderResponse.getBody().size());
        PlaceRequest secondPlaceRequest = new PlaceRequest(2, "Kotov", "Vasya", "777777", "2");
        ResponseEntity<PlaceResponse> secondPlaceResponse = selectPlace(secondPlaceRequest, secondCookieClient);
        assertEquals(2, secondPlaceResponse.getBody().getOrderId());        
        ResponseEntity<List<Integer>> secondFreePlacesList =  getFreePlaces(secondCookieClient, secondOrderId1);
        assertEquals(18, secondFreePlacesList.getBody().size());
        logoutUser(secondCookieClient);


        RegistrationUpdateRequest thirdClient = RegistrationUpdateRequest.builder().lastname("Федоров").firstname("Федор").patronymic("Федорович").numberPhone("88005343287").email("fedoriab@gmai.com").login("fedor1999").password("fedafeda123").build();
        ResponseEntity<UserResponse> thirdResponse = registerClient(thirdClient);
        String thirdCookieClient = getCookie(thirdResponse);

        ResponseEntity<List<TripResponse>> thirdListTrips =  getTrips(thirdCookieClient, new Parameters(null, null, null, null, null));
        Integer thirdTripId = thirdListTrips.getBody().get(0).getTripId();
        assertEquals(3, thirdListTrips.getBody().size());

        RegistrationUpdateRequest thirdClientUpdate = new RegistrationUpdateRequest("Федоровский", "Федя", "Федорович", "fedya@mail.ru", "89325642323", null, null, null, "fedafeda123", "fedkafedka");
        ResponseEntity<UserResponse> thirdUpdateClientResp =  updateClient(thirdCookieClient, thirdClientUpdate);
        assertEquals("Федоровский", thirdUpdateClientResp.getBody().getLastname());
        assertEquals("fedya@mail.ru", thirdUpdateClientResp.getBody().getEmail());

        List<PassengerRequest> thirdPassengers = new ArrayList<>();
        thirdPassengers.add(PassengerRequest.builder().firstName("Anton").lastName("Zhukov").passport("555444").build());
        OrderRequest thirdOrderRequest = new OrderRequest(thirdTripId, "2024-06-03", thirdPassengers);
        
        ResponseEntity<OrderResponse> thirdOrderResponse = orderingTicket(thirdOrderRequest, thirdCookieClient);
        Integer thirdOrderId = thirdOrderResponse.getBody().getOrderId();
        assertEquals(4, thirdOrderId);
        ResponseEntity<List<OrderResponse>> thirdOrdersListBeforeCancel =  getOrdersList(thirdCookieClient, new Parameters(null, null, null, null, null, null));
        assertEquals(1, thirdOrdersListBeforeCancel.getBody().size());
        ResponseEntity<String> thirdResponseCancelOrder = cancelOrder(thirdCookieClient, thirdOrderId);
        assertEquals("{}", thirdResponseCancelOrder.getBody());
        ResponseEntity<List<OrderResponse>> thirdOrdersListAfterCancel =  getOrdersList(thirdCookieClient, new Parameters(null, null, null, null, null, null));
        assertEquals(0, thirdOrdersListAfterCancel.getBody().size());

        logoutUser(thirdCookieClient);


        ResponseEntity<UserResponse> responseUserLogin =  loginUser(new AuthenticationRequest(requestAdmin.getLogin(), requestAdmin.getPassword()));
        String secondAdminCookie = getCookie(responseUserLogin);

        ResponseEntity<List<OrderResponse>> ordersResponse = getOrdersList(secondAdminCookie, new Parameters(secondResponse.getBody().getId(), "Mira", "Pushkina", null, null, null));
        assertEquals(2, ordersResponse.getBody().size());
        assertEquals("Mira", ordersResponse.getBody().get(0).getFromStation());
        assertEquals("Pushkina", ordersResponse.getBody().get(0).getToStation());
        logoutUser(secondAdminCookie);

    }

}
