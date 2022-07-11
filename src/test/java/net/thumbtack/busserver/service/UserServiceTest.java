package net.thumbtack.busserver.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.dto.request.RegistrationUpdateRequest;
import net.thumbtack.busserver.dto.response.UserResponse;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.Role;
import net.thumbtack.busserver.model.User;
import net.thumbtack.busserver.security.SessionProvider;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MaintaskApplication.class)
public class UserServiceTest {

    @Autowired
    UserService userService;
    @MockBean
    private UserDao userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private SessionProvider sessionProvider;

    @Test
    void testGetInfoAboutYourself() {
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        UserResponse user = userService.getInfoAboutYourself(cookie, mock(HttpServletResponse.class));
        assertEquals("ROLE_USER", user.getUserType());
    }

    @Test
    void testRegistrationAdministration() throws BusCompanyException {
        RegistrationUpdateRequest registrationUpdateRequest = new RegistrationUpdateRequest("Ivanov", "Ivan", "Ivanovich", null, null, "Director", "ivanov", "ivanivan", null, null);
        doAnswer(invocation-> {
            Object[] args = invocation.getArguments();
            ((Administration)args[0]).setId(1);
            return null;
        }).when(userDao).insertAdministration(Mockito.any(Administration.class));
        doAnswer(invocation -> Role.ADMIN).when(userDao).getRoleByUserId(Mockito.anyInt());
        UserResponse user = userService.registrationAdministration(registrationUpdateRequest, mock(HttpServletResponse.class));
        assertEquals("ROLE_ADMIN", user.getUserType());
    }

    @Test
    void testRegistrationAdministration_BusCompanyException_UserExist() throws BusCompanyException {
        RegistrationUpdateRequest registrationUpdateRequest = new RegistrationUpdateRequest("Ivanov", "Ivan", "Ivanovich", null, null, "Director", "ivanov", "ivanivan", null, null);
        when(userDao.checkExistenceByLogin(Mockito.anyString())).thenReturn(true);
        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> userService.registrationAdministration(registrationUpdateRequest, mock(HttpServletResponse.class)));
        assertEquals("user with current login exists", exception.getError().getMessage());
    }

    @Test
    void testRegistrationClient() throws BusCompanyException {
        RegistrationUpdateRequest registrationUpdateRequest = new RegistrationUpdateRequest("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", null, "ivanov", "ivanivan", null, null);
        doAnswer(invocation-> {
            Object[] args = invocation.getArguments();
            ((Client)args[0]).setId(1);
            return null;
        }).when(userDao).insertClient(Mockito.any(Client.class));
        doAnswer(invocation -> Role.USER).when(userDao).getRoleByUserId(Mockito.anyInt());
        UserResponse user = userService.registrationClient(registrationUpdateRequest, mock(HttpServletResponse.class));
        assertEquals("ROLE_USER", user.getUserType());
    }

    @Test
    void testGetAllClients() {
        List<Client> clients = new ArrayList<Client>();
        Client firstClient = new Client("Ivanov", "Ivan", "Ivanovich", "ivan@gmail.com", "88005553535", "ivanov", "ivanivan");
        firstClient.setRole(Role.USER);
        clients.add(firstClient);
        Client secondClient = new Client("Petrov", "Petr", "Petrovich", "petyaa@gmail.com", "88005553535", "petka", "petyapetya");
        secondClient.setRole(Role.USER);
        clients.add(secondClient);

        Cookie cookie = mock(Cookie.class);
        when(userDao.getAllClients()).thenReturn(clients);

        List<UserResponse> receivedClients = userService.getAllClients(cookie, mock(HttpServletResponse.class));

        assertEquals(2, receivedClients.size());

    }

    @Test
    void testUpdateAdministration() throws BusCompanyException {
        RegistrationUpdateRequest registrationUpdateRequest = new RegistrationUpdateRequest("Petrov", "Petya", "Peetrovich", null, null, "director", "ivanov", null, "ivanivan", "ivanovivan");
        User admin = new Administration("Ivanov", "Ivan", "Ivanovich", "director", "ivanov", passwordEncoder.encode("ivanivan"));
        admin.setRole(Role.ADMIN);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(admin);

        userService.updateAdministration(registrationUpdateRequest, cookie, mock(HttpServletResponse.class));
        assertEquals(registrationUpdateRequest.getNewPassword(), admin.getPassword());
    }

    @Test
    void testUpdateAdministration_BusCompanyException_InvalidNewPassword() throws BusCompanyException {
        RegistrationUpdateRequest registrationUpdateRequest = new RegistrationUpdateRequest("Petrov", "Petya", "Peetrovich", null, null, "director", "ivanov", null, "invalidPassword", "ivanovivan");
        User admin = new Administration("Ivanov", "Ivan", "Ivanovich", "director", "ivanov", passwordEncoder.encode("ivanivan"));
        admin.setRole(Role.ADMIN);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(admin);

        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> userService.updateAdministration(registrationUpdateRequest, cookie, mock(HttpServletResponse.class)));
        assertEquals("password do not match", exception.getError().getMessage());
    }

    @Test
    void testUpdateClient() throws BusCompanyException {
        RegistrationUpdateRequest registrationUpdateRequest = new RegistrationUpdateRequest("Ivanov", "Ivan", "Ivanovich", "ivanov@gmail.com", "89049004543", null, null, null, "ivanivan", "ivanovivan");

        User client = new Client("Kozlov", "Artem", "Nikolaevich", "kozlov@gmail.com", "88005553535", "kozlov", passwordEncoder.encode("ivanivan"));
        client.setRole(Role.USER);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);

        userService.updateClient(registrationUpdateRequest, cookie, mock(HttpServletResponse.class));
        assertEquals(registrationUpdateRequest.getNewPassword(), client.getPassword());

    }

    @Test
    void testDeleteUser() throws BusCompanyException {
        User client = new Client("Kozlov", "Artem", "Nikolaevich", "kozlov@gmail.com", "88005553535", "kozlov", passwordEncoder.encode("ivanivan"));

        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        assertDoesNotThrow(() -> userService.deleteUser(cookie));
    }

    @Test
    void testDeleteUser_BusCompanyException_LastAdministration() throws BusCompanyException {
        User admin = new Administration("Ivanov", "Ivan", "Ivanovich", "director", "ivanov", passwordEncoder.encode("ivanivan"));

        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(admin);
        when(userDao.isLastAdministration()).thenReturn(1);
        BusCompanyException exception = assertThrows(BusCompanyException.class, () -> userService.deleteUser(cookie));

        assertEquals("you are the last administration", exception.getError().getMessage());
    }

    @Test
    void testLoginUser() {
        User client = new Client("Ivanov", "Ivan", "Ivanovich", "jek1@gmail.com", "88005553535", "ivanov", "ivanivan");
        client.setRole(Role.USER);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        when(userDao.getUserBySessionId("sessionId")).thenReturn(client);
        UserResponse user = userService.loginUser(cookie, mock(HttpServletResponse.class));
        assertEquals("ROLE_USER", user.getUserType());
    }

    @Test
    void testLogout() {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("sessionId");
        doNothing().when(userDao).deleteSession(Mockito.anyString());
        userService.logout(cookie, mock(HttpServletResponse.class));
        verify(userDao, times(1)).deleteSession(Mockito.anyString());
    }
}
