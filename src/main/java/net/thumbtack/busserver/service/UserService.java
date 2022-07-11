package net.thumbtack.busserver.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.thumbtack.busserver.dao.OrderDao;
import net.thumbtack.busserver.dao.TripDao;
import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.dto.mappers.RegistrationMapper;
import net.thumbtack.busserver.dto.mappers.UpdateUserMapper;
import net.thumbtack.busserver.dto.request.RegistrationUpdateRequest;
import net.thumbtack.busserver.dto.response.UserResponse;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.Session;
import net.thumbtack.busserver.model.User;
import net.thumbtack.busserver.security.SessionProvider;

@Service
public class UserService {

    private UserDao userDao;

    private TripDao tripDao;
    
    private OrderDao orderDao;

    private PasswordEncoder passwordEncoder;

    private SessionProvider sessionProvider;

    private RegistrationMapper registrationMapper;

    private UpdateUserMapper updateMapper;


    @Value("${user_idle_timeout}")
    private int userIdleTimeout;

    @Autowired
    public UserService(UserDao userDao, TripDao tripDao, OrderDao orderDao, @Lazy PasswordEncoder passwordEncoder, SessionProvider sessionProvider) {
        this.userDao = userDao;
        this.tripDao = tripDao;
        this.orderDao = orderDao;
        this.passwordEncoder = passwordEncoder;
        this.sessionProvider = sessionProvider;
        this.registrationMapper = Mappers.getMapper(RegistrationMapper.class);
        this.updateMapper = Mappers.getMapper(UpdateUserMapper.class);

    }

    public UserService() {
    }

    public UserResponse registrationAdministration(RegistrationUpdateRequest requestAdmin, HttpServletResponse response)
            throws BusCompanyException {
        Administration administration = registrationMapper.registrationRequestToAdministration(requestAdmin);
        administration.setPassword(passwordEncoder.encode(administration.getPassword()));
        if (userDao.checkExistenceByLogin(administration.getLogin())) {
            throw new BusCompanyException(ErrorCode.USER_EXIST);
        }
        userDao.insertAdministration(administration);
        administration.setRole(userDao.getRoleByUserId(administration.getId()));

        String date = LocalDateTime.now().plusSeconds(userIdleTimeout).toString();
        String sessionId = sessionProvider.createSession(response);
        Session session = new Session(sessionId, date);
        userDao.deleteSessionsByExpiration();
        userDao.insertSession(administration.getId(), session);
        return registrationMapper.administrationToUserResponse(administration);
    }

    public UserResponse registrationClient(RegistrationUpdateRequest registerRequest, HttpServletResponse response)
            throws BusCompanyException {
        registerRequest.setNumberPhone(registerRequest.getNumberPhone().replaceAll("-", ""));
        Client client = registrationMapper.registrationRequestToClient(registerRequest);
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        if (userDao.checkExistenceByLogin(client.getLogin())) {
            throw new BusCompanyException(ErrorCode.USER_EXIST);
        }
        userDao.insertClient(client);
        client.setRole(userDao.getRoleByUserId(client.getId()));
        LocalDateTime time = LocalDateTime.now();
        String date = time.plusSeconds(userIdleTimeout).toString();
        String sessionId = sessionProvider.createSession(response);
        Session session = new Session(sessionId, date);
        userDao.deleteSessionsByExpiration();
        userDao.insertSession(client.getId(), session);

        return registrationMapper.clientToUserResponse(client);
    }

    public List<UserResponse> getAllClients(Cookie cookie, HttpServletResponse response) {
        String sessionId = cookie.getValue();
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);
        return registrationMapper.listClientToUserResponse(userDao.getAllClients());
    }

    public UserResponse updateAdministration(RegistrationUpdateRequest request, Cookie cookie,
            HttpServletResponse response) throws BusCompanyException {
        String sessionId = cookie.getValue();
        Administration adminFromDB = (Administration) userDao.getUserBySessionId(sessionId);
        if (!passwordEncoder.matches(request.getOldPassword(), adminFromDB.getPassword())) {
            throw new BusCompanyException(ErrorCode.INVALID_OLD_PASSWORD);
        }
        request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
        updateMapper.updateAdministrationFromRequest(adminFromDB, request);
        userDao.updateAdministration(adminFromDB);
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);
        return updateMapper.adminToUpdateUserResponse(adminFromDB);
    }

    public UserResponse updateClient(RegistrationUpdateRequest registerUpdateRequest, Cookie cookie,
            HttpServletResponse response) throws BusCompanyException {
        String sessionId = cookie.getValue();
        Client clientFromDB = (Client) userDao.getUserBySessionId(sessionId);
        if (!passwordEncoder.matches(registerUpdateRequest.getOldPassword(), clientFromDB.getPassword())) {
            throw new BusCompanyException(ErrorCode.INVALID_OLD_PASSWORD);
        }
        registerUpdateRequest.setNewPassword(passwordEncoder.encode(registerUpdateRequest.getNewPassword()));
        updateMapper.updateClientFromRequest(clientFromDB, registerUpdateRequest);

        userDao.updateClient(clientFromDB);
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);
        return updateMapper.userToUpdateUserResponse(clientFromDB);
    }

    public void deleteUser(Cookie cookie) throws BusCompanyException {
        String sessionId = cookie.getValue();
        User userFromDB = userDao.getUserBySessionId(sessionId);
        if (userFromDB instanceof Administration) {
            if (isLastAdministration()) {
                tripDao.deleteTripByUserId(userFromDB.getId());
                userDao.deleteUser(userFromDB.getLogin());
            } else {
                throw new BusCompanyException(ErrorCode.LAST_ADMINISTRATION);
            }
        } else {
            orderDao.deleteOrderByUserId(userFromDB.getId());
            userDao.deleteUser(userFromDB.getLogin());
        }
    }

    public boolean isLastAdministration() {
        if (userDao.isLastAdministration() == 1) {
            return false;
        }
        return true;
    }

    public UserResponse getInfoAboutYourself(Cookie cookie, HttpServletResponse response) {
        String sessionId = cookie.getValue();
        User userFromDB = userDao.getUserBySessionId(sessionId);
        userDao.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(userIdleTimeout).toString()));
        sessionProvider.extendExpirationCookie(cookie, response);
        return registrationMapper.userToUserResponse(userFromDB);
    }

    public UserResponse loginUser(Cookie cookie, HttpServletResponse response) {
        String sessionId = cookie.getValue();
        User userFromDB = userDao.getUserBySessionId(sessionId);
        return registrationMapper.userToUserResponse(userFromDB);

    }

    public void logout(Cookie cookie, HttpServletResponse response) {
        String sessionId = cookie.getValue();
        userDao.deleteSession(sessionId);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setValue("");
        response.addCookie(cookie);
    }

}
