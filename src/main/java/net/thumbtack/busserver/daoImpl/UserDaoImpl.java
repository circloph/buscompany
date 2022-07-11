package net.thumbtack.busserver.daoImpl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.ServerException;
import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.Role;
import net.thumbtack.busserver.model.Session;
import net.thumbtack.busserver.model.User;

@Component
public class UserDaoImpl extends DaoImplBase implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public void insertAdministration(Administration administration) {
        try (SqlSession session = getSession()) {
            try {
                getUserMapper(session).insertAdministration(administration);
                getAdministrationMapper(session).insertAdministration(administration);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void insertClient(Client client) {
        try (SqlSession session = getSession()) {
            try {
                getUserMapper(session).insertClient(client);
                getClientMapper(session).insertClient(client);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }

    }

    @Override
    public User getUserByLogin(String login) {
        User user = null;
        Role role = null;
        try (SqlSession session = getSession()) {
            try {
                role = getRoleMapper(session).getRoleByLogin(login);
                if (role.getName().equals("ROLE_USER")) {
                    user = getClientMapper(session).getClientByLogin(login);
                } else {
                    user = getAdministrationMapper(session).getAdministrationByLogin(login);
                }
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return user;
    }

    @Override
    public List<Client> getAllClients() {
        List<Client> clients = null;
        LOGGER.debug("DAO select all clients");
        try (SqlSession session = getSession()) {
            try {
                clients = getClientMapper(session).getAllClients();
            } catch (RuntimeException e) {
                LOGGER.debug("Can't select all clients");
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return clients;
    }

    @Override
    public void updateAdministration(Administration administration) {
        try (SqlSession session = getSession()) {
            try {
                getAdministrationMapper(session).updateAdministration(administration);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }

    }

    @Override
    public void updateClient(Client client) {
        try (SqlSession session = getSession()) {
            try {
                getClientMapper(session).updateClient(client);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public boolean checkExistenceByLogin(String login) {
        boolean result;
        try (SqlSession session = getSession()) {
            try {
                result= getUserMapper(session).checkExistenceByLogin(login);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return result;
    }

    @Override
    public void deleteUser(String login) {
        try (SqlSession session = getSession()) {
            try {
                getUserMapper(session).deleteUser(login);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public Integer isLastAdministration() {
        Integer result = null;
        try (SqlSession session = getSession()) {
            try {
                result = getUserMapper(session).isLastAdministration();
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return result;
    }

    @Override
    public Role getRoleByUserId(Integer id) {
        Role role = null;
        try (SqlSession session = getSession()) {
            try {
                role = getRoleMapper(session).getRoleByUserId(id);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return role;
    }

    @Override
    public Session getSessionBySessionId(String sessionId) {
        Session sessionFromDB = null;
        try (SqlSession session = getSession()) {
            try {
                sessionFromDB = getSessionMapper(session).getSessionBySessionId(sessionId);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return sessionFromDB;
    }

    @Override
    public void updateExpiration(Session customSession) {
        try (SqlSession session = getSession()) {
            try {
                getSessionMapper(session).updateExpiration(customSession);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void deleteSession(String sessionId) {
        try (SqlSession session = getSession()) {
            try {
                getSessionMapper(session).deleteSession(sessionId);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void deleteSessionsByExpiration() {
        try (SqlSession session = getSession()) {
            try {
                getSessionMapper(session).deleteSessionsByExpiration();
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public Integer getUserIdBySessionId(String sessionId) {
        Integer userId = null;
        try (SqlSession session = getSession()) {
            try {
                userId = getSessionMapper(session).getUserIdBySessionId(sessionId);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
        return userId;
    }

    @Override
    public void insertSession(Integer userId, Session customSession) {
        try (SqlSession session = getSession()) {
            try {
                getSessionMapper(session).insertSession(userId, customSession);
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public User getUserBySessionId(String sessionId) {
        User user = null;
        Role role = null;
        try (SqlSession session = getSession()) {
            try {
                role = getRoleMapper(session).getRoleBySessionId(sessionId);
                if (role.getName().equals("ROLE_USER")) {
                    user = getClientMapper(session).getClientBySessionId(sessionId);
                } else {
                    user = getAdministrationMapper(session).getAdministrationBySessionId(sessionId);
                }
            } catch (RuntimeException e) {
                session.rollback();
                throw new ServerException(ErrorCode.SERVER_ERROR);
            }
            session.commit(); 
        }
        return user;
    }

}
