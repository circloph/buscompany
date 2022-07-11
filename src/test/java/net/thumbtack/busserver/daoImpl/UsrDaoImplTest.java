package net.thumbtack.busserver.daoImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.Role;
import net.thumbtack.busserver.model.Session;
import net.thumbtack.busserver.model.User;

public class UsrDaoImplTest {

    private static UserDaoImpl userDaoImpl;

    @BeforeAll
    static void setUp() {
        userDaoImpl = new UserDaoImpl();
    }

    @BeforeEach
    void dataBaseCleanup() {
        DebugDaoImpl serverDaoImpl = new DebugDaoImpl();
        serverDaoImpl.dataBaseCleanup();
    }

    @Test
    void testInsertClient()  {
        Client client = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
        userDaoImpl.insertClient(client);
        assertEquals(1, client.getId());
    }

    @Test
    void testInsertAdministration()  {
        Administration administration = new Administration("Petrov", "Petya", "Petrovich", "Manager", "petrov", "petya1995");
        userDaoImpl.insertAdministration(administration);
        assertEquals(1, administration.getId());
    }

    @Test
    void testGetUserByLogin() {
        Client client = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
        userDaoImpl.insertClient(client);
        assertEquals(client.getId(), userDaoImpl.getUserByLogin(client.getLogin()).getId());
    }

    @Test
    void testCheckExistenceByLogin() {
        Client client = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
        assertEquals(false, userDaoImpl.checkExistenceByLogin(client.getLogin()));
        userDaoImpl.insertClient(client);
        assertEquals(true, userDaoImpl.checkExistenceByLogin(client.getLogin()));
    }

    @Test
    void testDeleteSession() {
        Client client = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
        String sessionId = UUID.randomUUID().toString();
        Session customSession = new Session(sessionId, LocalDateTime.now().plusSeconds(60).toString());
        userDaoImpl.insertSession(client.getId(), customSession);
        assertTrue(userDaoImpl.getSessionBySessionId(sessionId) != null);
        userDaoImpl.deleteSession(sessionId);
        assertTrue(userDaoImpl.getSessionBySessionId(sessionId) == null);
    }

    // @Test
    // void testDeleteSessionsByExpiration() throws InterruptedException {
    //     Client client = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
    //     String sessionId = UUID.randomUUID().toString();
    //     Session customSession = new Session(sessionId, LocalDateTime.now().plusSeconds(60).toString());
    //     userDaoImpl.insertSession(client.getLogin(), customSession);
    //     userDaoImpl.deleteSessionsByExpiration();
    //     Session session = userDaoImpl.getSessionBySessionId(sessionId); 
    //     assertTrue(session != null);
    //     Thread.sleep(60000);
    //     userDaoImpl.deleteSessionsByExpiration();
    //     assertTrue(userDaoImpl.getSessionBySessionId(sessionId) == null);
    // }

    @Test
    void testDeleteUser() {
        Client firstClient = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
        Client secondClient = new Client("Ivanov", "Ivan", "Ivanovich", "ivanov@gmail.com", "88005553535", "ivanov", "ivanov1998");
        userDaoImpl.insertClient(firstClient);
        userDaoImpl.insertClient(secondClient);
        User userFromDB =  userDaoImpl.getUserByLogin(firstClient.getLogin());
        assertTrue(userFromDB != null);
        userDaoImpl.deleteUser(firstClient.getLogin());
        User deletedUser =  userDaoImpl.getUserByLogin(firstClient.getLogin());
        assertTrue(deletedUser == null);
        
    }                                   

    @Test
    void testGetAllClients() {
        Client firstClient = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
        Client secondClient = new Client("Ivanov", "Ivan", "Ivanovich", "ivanov@gmail.com", "88005553535", "ivanov", "ivanov1998");
        userDaoImpl.insertClient(firstClient);
        userDaoImpl.insertClient(secondClient);
        List<Client> clients = userDaoImpl.getAllClients();
        assertEquals(2, clients.size());
    }


    @Test
    void testGetRoleByUserId() {
        Client client = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
        userDaoImpl.insertClient(client);
        Role role = userDaoImpl.getRoleByUserId(client.getId());
        String roleName = role.getName();
        assertEquals("ROLE_USER", roleName);
    }

    @Test
    void testGetSessionBySessionId() {
        String sessionId = UUID.randomUUID().toString();
        Session customSession = new Session(sessionId, LocalDateTime.now().plusSeconds(60).toString());
        userDaoImpl.insertSession(1, customSession);
        Session sessionFromDB = userDaoImpl.getSessionBySessionId(sessionId);
        assertEquals(customSession, sessionFromDB);
    }

    @Test
    void testGetUserIdBySessionId() {
        String sessionId = UUID.randomUUID().toString();
        Session customSession = new Session(sessionId, LocalDateTime.now().plusSeconds(60).toString());
        userDaoImpl.insertSession(1, customSession);
        Integer userIdFromDb = userDaoImpl.getUserIdBySessionId(sessionId);
        assertEquals(1, userIdFromDb);
    }

    @Test
    void testInsertSession() {
        String sessionId = UUID.randomUUID().toString();
        Session customSession = new Session(sessionId, LocalDateTime.now().plusSeconds(60).toString());
        userDaoImpl.insertSession(1, customSession);
        Session sessionFromDB = userDaoImpl.getSessionBySessionId(sessionId);
        assertEquals(customSession, sessionFromDB);
    }

    @Test
    void testIsLastAdministration() {
        Administration firstAdmin = new Administration("Petrov", "Petya", "Petrovich", "Manager", "petrov", "petya1995");
        Administration secondAdmin = new Administration("Pupkin", "Vasya", "Petrovich", "Developer", "pupkin", "pupkin");
        userDaoImpl.insertAdministration(firstAdmin);
        userDaoImpl.insertAdministration(secondAdmin);
        Integer numberOfAdmins = userDaoImpl.isLastAdministration();
        assertEquals(2, numberOfAdmins);
    }

    @Test
    void testUpdateAdministration() {
        Administration admin = new Administration("Petrov", "Petya", "Petrovich", "Manager", "petrov", "petya1995");
        userDaoImpl.insertAdministration(admin);
        String firstNameBeforeChanges = admin.getFirstname();
        admin.setFirstname("Vaska");
        userDaoImpl.updateAdministration(admin);
        Administration afterChanged = (Administration) userDaoImpl.getUserByLogin(admin.getLogin());
        assertNotEquals(firstNameBeforeChanges, afterChanged.getFirstname());
    }

    @Test
    void testUpdateClient() {
        Client client = new Client("Petrov", "Petya", "Petrovich", "petrov@gmail.com", "88005553535", "petrov", "petya1995");
        userDaoImpl.insertClient(client);
        String firstNameBeforeChanges = client.getFirstname();
        client.setFirstname("Vaska");
        userDaoImpl.updateClient(client);
        Client afterChanged = (Client) userDaoImpl.getUserByLogin(client.getLogin());
        assertNotEquals(firstNameBeforeChanges, afterChanged.getFirstname());
    }

    @Test
    void testUpdateExpiration() {
        String sessionId = UUID.randomUUID().toString();
        Session customSession = new Session(sessionId, LocalDateTime.now().plusSeconds(60).toString());
        userDaoImpl.insertSession(1, customSession);
        String expirationBeforeChanges = customSession.getExpiration();
        userDaoImpl.updateExpiration(new Session(sessionId, LocalDateTime.now().plusSeconds(61).toString()));
        Session sessionFromDB = userDaoImpl.getSessionBySessionId(sessionId);
        assertNotEquals(expirationBeforeChanges, sessionFromDB.getExpiration());
    }
    
}
