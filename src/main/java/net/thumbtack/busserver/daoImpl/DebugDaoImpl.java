package net.thumbtack.busserver.daoImpl;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import net.thumbtack.busserver.dao.DebugDao;

@Component
public class DebugDaoImpl extends DaoImplBase implements DebugDao {

    @Override
    public void dataBaseCleanup() {
        try (SqlSession session = getSession()) {
            try {
                getDataBaseCleanup(session).disableForeignKeyChecking();
                getDataBaseCleanup(session).clearClients();
                getDataBaseCleanup(session).clearAdministration();
                getDataBaseCleanup(session).clearUsers();
                getDataBaseCleanup(session).clearTrips();
                getDataBaseCleanup(session).clearTripDates();
                getDataBaseCleanup(session).clearSchedules();
                getDataBaseCleanup(session).clearOrders();
                getDataBaseCleanup(session).clearPassengers();
                getDataBaseCleanup(session).clearOrdersPassengers();
                getDataBaseCleanup(session).clearPlaces();
                getDataBaseCleanup(session).clearDates();
                getDataBaseCleanup(session).enableForeignKeyChecking();
            } catch (RuntimeException e) {
                session.rollback();
                throw e;
            }
            session.commit();
        }
    }
    
}
