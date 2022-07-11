package net.thumbtack.busserver.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import net.thumbtack.busserver.AppConfig;
import net.thumbtack.busserver.MaintaskApplication;
import net.thumbtack.busserver.daoImpl.DebugDaoImpl;
import net.thumbtack.busserver.dto.request.AuthenticationRequest;
import net.thumbtack.busserver.dto.request.RegistrationUpdateRequest;
import net.thumbtack.busserver.dto.response.UserResponse;
import net.thumbtack.busserver.exception.ErrorValidation;

@SpringBootTest(classes = MaintaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    void dataBaseCleanup() {
        DebugDaoImpl serverDaoImpl = new DebugDaoImpl();
        serverDaoImpl.dataBaseCleanup();
    }

    public ResponseEntity<UserResponse> registerClient(RegistrationUpdateRequest request) {
        String userResourceUrl = "http://localhost:8080/api/clients";
        HttpEntity<RegistrationUpdateRequest> requestEntity = new HttpEntity<RegistrationUpdateRequest>(request);
        return restTemplate.exchange(userResourceUrl, HttpMethod.POST, requestEntity, UserResponse.class);
    }

    public ResponseEntity<UserResponse> registerAdmin(RegistrationUpdateRequest request) {
        String userResourceUrl = "http://localhost:8080/api/admins";
        HttpEntity<RegistrationUpdateRequest> requestEntity = new HttpEntity<RegistrationUpdateRequest>(request);
        return restTemplate.exchange(userResourceUrl, HttpMethod.POST, requestEntity, UserResponse.class);
    }

    public String getCookie(ResponseEntity<UserResponse> responseEntity) {
        HttpHeaders headers = responseEntity.getHeaders();
        return headers.getFirst(HttpHeaders.SET_COOKIE);
    }

    public ResponseEntity<String> logoutUser(String cookie) {
        String userResourceUrl = "http://localhost:8080/api/sessions";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
        return restTemplate.exchange(userResourceUrl, HttpMethod.DELETE, requestEntity, String.class);
    }

    
    @Test
    void testRegistrationAdmin_Success() {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> response = registerAdmin(request);
        UserResponse userResponse = (UserResponse) response.getBody();
        assertEquals(request.getFirstname(), userResponse.getFirstname());
        assertEquals(request.getLastname(), userResponse.getLastname());
        assertEquals(request.getPatronymic(), userResponse.getPatronymic());
        assertEquals(request.getPosition(), userResponse.getPosition());
        assertTrue(userResponse.getUserType().equals("ROLE_ADMIN"));
    }

    @Test
    void testRegistrationAdmin_BusCompanyException_UserExist() throws JsonMappingException, JsonProcessingException {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> response = registerAdmin(request);
        logoutUser(getCookie(response));
        RegistrationUpdateRequest secondRequest = RegistrationUpdateRequest.builder().lastname("Петров").firstname("Александр").patronymic("Сергеевич").position("director").login("admin").password("qwertyqwerty").build();
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> registerAdmin(secondRequest));
        String s = exception.getResponseBodyAsString();
        ObjectMapper om = new ObjectMapper();
        ErrorValidation errorValidation = om.readValue(s, ErrorValidation.class);
        assertEquals("user with current login exists", errorValidation.getAllErrors().get(0).getMessage());
    }

    @Test
    void testRegistrationAdmin_BusCompanyException_InvalidAnthroponymValues() throws JsonMappingException, JsonProcessingException {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Konstantin").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> registerAdmin(request));
        String s = exception.getResponseBodyAsString();
        ObjectMapper om = new ObjectMapper();
        ErrorValidation errorValidation = om.readValue(s, ErrorValidation.class);
        assertEquals("anthroponym must contain only Russian letters, numbers, space, dash and not be empty", errorValidation.getAllErrors().get(0).getMessage());
    }

    @Test
    void testRegistrationClient_Success() {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").numberPhone("88005553535").email("ivana@gmai.com").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> response = registerClient(request);
        UserResponse userResponse = (UserResponse) response.getBody();
        assertEquals(request.getFirstname(), userResponse.getFirstname());
        assertEquals(request.getLastname(), userResponse.getLastname());
        assertEquals(request.getPatronymic(), userResponse.getPatronymic());
        assertEquals(request.getEmail(), userResponse.getEmail());
        assertEquals(request.getPatronymic(), userResponse.getPatronymic());
        assertEquals(request.getPatronymic(), userResponse.getPatronymic());
        assertTrue(userResponse.getUserType().equals("ROLE_USER"));
    }

    @Test
    void testRegistrationClient_BusCompanyException_UserExist() throws JsonMappingException, JsonProcessingException {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").numberPhone("88005553535").email("ivana@gmai.com").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> response = registerClient(request);
        logoutUser(getCookie(response));
        RegistrationUpdateRequest secondRequest = RegistrationUpdateRequest.builder().lastname("Петров").firstname("Александр").patronymic("Сергеевич").numberPhone("88005553535").email("ivana@gmai.com").login("admin").password("adminadmin").build();
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> registerClient(secondRequest));
        String s = exception.getResponseBodyAsString();
        ObjectMapper om = new ObjectMapper();
        ErrorValidation errorValidation = om.readValue(s, ErrorValidation.class);
        assertEquals("user with current login exists", errorValidation.getAllErrors().get(0).getMessage());
    }

    @Test
    void testRegistrationClient_BusCompanyException_ShortPassword() throws JsonMappingException, JsonProcessingException {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").numberPhone("88005553535").email("ivana@gmai.com").login("admin").password("admina").build();
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> registerClient(request));
        String s = exception.getResponseBodyAsString();
        ObjectMapper om = new ObjectMapper();
        ErrorValidation errorValidation = om.readValue(s, ErrorValidation.class);
        assertEquals("password length must be longer", errorValidation.getAllErrors().get(0).getMessage());
    }

    @Test
    void testGetInfoAboutYourself() {
        RegistrationUpdateRequest request = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").position("manager").login("admin").password("adminadmin").build();
        ResponseEntity<UserResponse> adminResponse = registerAdmin(request);
        String set_cookie = getCookie(adminResponse);

        String getInfoResourceUrl = "http://localhost:8080/api/accounts";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", set_cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
        HttpEntity<UserResponse> response = restTemplate.exchange(getInfoResourceUrl, HttpMethod.GET, requestEntity, UserResponse.class);

        assertEquals(adminResponse.getBody().getId(), response.getBody().getId());
    }

    @Test
    void testGetAllClients() {

        RegistrationUpdateRequest firstClient = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").numberPhone("89005842323").email("ekeorg@gmai.com").login("arkadiev").password("ivan1000").build();
        RegistrationUpdateRequest secondClient = RegistrationUpdateRequest.builder().lastname("Круглов").firstname("Артем").patronymic("Аркадьевич").numberPhone("89105842323").email("manaf@gmai.com").login("akakfoaof").password("kruglovart").build();
        RegistrationUpdateRequest trirdClient = RegistrationUpdateRequest.builder().lastname("Молотов").firstname("Станислав").patronymic("Николаевич").numberPhone("88005553535").email("arkada@gmai.com").login("nikolaevich").password("qwerty123").build();
        ResponseEntity<UserResponse> firstResponse = registerClient(firstClient);
        logoutUser(getCookie(firstResponse));
        ResponseEntity<UserResponse> secondResponse = registerClient(secondClient);
        logoutUser(getCookie(secondResponse));
        ResponseEntity<UserResponse> thirdResponse = registerClient(trirdClient);
        logoutUser(getCookie(thirdResponse));

        RegistrationUpdateRequest requestAdmin = RegistrationUpdateRequest.builder().lastname("Сергеев").firstname("Александр").patronymic("Иванович").position("manager").login("sergeeev1").password("wertyu123").build();
        ResponseEntity<UserResponse> adminResponseEntity = registerAdmin(requestAdmin);
        String cookie = getCookie(adminResponseEntity);

        String getInfoResourceUrl = "http://localhost:8080/api/clients";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", cookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
        HttpEntity<List<UserResponse>> response = restTemplate.exchange(getInfoResourceUrl, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<UserResponse>>() {});
        assertEquals(3, response.getBody().size());
    }

    @Test
    void testLogoutUser() {
        RegistrationUpdateRequest firstClient = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").numberPhone("89005842323").email("ekeorg@gmai.com").login("arkadiev").password("ivan1000").build();
        ResponseEntity<UserResponse> firstResponse = registerClient(firstClient);
        ResponseEntity<String> response = logoutUser(getCookie(firstResponse));
        assertEquals("log out", response.getBody());
    }

    @Test 
    void testLoginUser() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);
        String loginUserUrl = "http://localhost:8080/api/sessions";
    

        RegistrationUpdateRequest firstClient = RegistrationUpdateRequest.builder().lastname("Иванов").firstname("Иван").patronymic("Иванович").numberPhone("89005842323").email("ekeorg@gmai.com").login("ivanovich").password("ivanivan").build();
        ResponseEntity<UserResponse> firstResponse = registerClient(firstClient);
        logoutUser(getCookie(firstResponse));

        AuthenticationRequest request = new AuthenticationRequest("ivanovich", "ivanivan");
        
        HttpEntity<AuthenticationRequest> requestEntity = new HttpEntity<AuthenticationRequest>(request);
        ResponseEntity<UserResponse> response =  restTemplate.exchange(loginUserUrl, HttpMethod.POST, requestEntity, UserResponse.class);
        assertEquals(firstClient.getFirstname(), response.getBody().getFirstname());
        assertEquals(firstClient.getLastname(), response.getBody().getLastname());
    }
}
