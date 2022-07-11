package net.thumbtack.busserver.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.Role;
import net.thumbtack.busserver.model.Session;
import net.thumbtack.busserver.model.User;

@Component
public interface UserDao {
    
    void insertAdministration(Administration administration);

    void insertClient(Client client);

    User getUserByLogin(String login);

    List<Client> getAllClients();

    void updateAdministration(Administration administration);

    void updateClient(Client client);

    boolean checkExistenceByLogin(String login);
    
    void deleteUser(String login);

    Integer isLastAdministration();

    Role getRoleByUserId(Integer id);

    void insertSession(Integer login, Session session);

    Integer getUserIdBySessionId(String sessionId);

    Session getSessionBySessionId(String sessionId);

    void updateExpiration(Session session);

    void deleteSession(String sessionId);

    void deleteSessionsByExpiration();

    User getUserBySessionId(String sessionId);

}
