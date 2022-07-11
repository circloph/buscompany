package net.thumbtack.busserver.daoImpl;

import net.thumbtack.busserver.configs.MyBatisConfig;
import net.thumbtack.busserver.mappers.AdministrationMapper;
import net.thumbtack.busserver.mappers.BusMapper;
import net.thumbtack.busserver.mappers.ClientMapper;
import net.thumbtack.busserver.mappers.DatabaseCleanup;
import net.thumbtack.busserver.mappers.DateMapper;
import net.thumbtack.busserver.mappers.OrderMapper;
import net.thumbtack.busserver.mappers.PassengerMapper;
import net.thumbtack.busserver.mappers.PlaceMapper;
import net.thumbtack.busserver.mappers.RoleMapper;
import net.thumbtack.busserver.mappers.ScheduleMapper;
import net.thumbtack.busserver.mappers.SessionMapper;
import net.thumbtack.busserver.mappers.TripMapper;
import net.thumbtack.busserver.mappers.UserMapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DaoImplBase {

    protected SqlSession getSession() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MyBatisConfig.class);
        return context.getBean("sqlSessionFactory", SqlSessionFactory.class).openSession();
    }

    protected UserMapper getUserMapper(SqlSession session) {
        return session.getMapper(UserMapper.class);
    }

    protected RoleMapper getRoleMapper(SqlSession session) {
        return session.getMapper(RoleMapper.class);
    }

    protected ClientMapper getClientMapper(SqlSession session) {
        return session.getMapper(ClientMapper.class);
    }

    protected AdministrationMapper getAdministrationMapper(SqlSession session) {
        return session.getMapper(AdministrationMapper.class);
    }

    protected BusMapper getBusMapper(SqlSession session) {
        return session.getMapper(BusMapper.class);
    }

    protected TripMapper getTripMapper(SqlSession session) {
        return session.getMapper(TripMapper.class);
    }

    protected ScheduleMapper getScheduleMapper(SqlSession session) {
        return session.getMapper(ScheduleMapper.class);
    }

    protected DateMapper getDateMapper(SqlSession session) {
        return session.getMapper(DateMapper.class);
    }

    protected OrderMapper getOrderMapper(SqlSession session) {
        return session.getMapper(OrderMapper.class);
    }

    protected PassengerMapper getPassengerMapper(SqlSession session) {
        return session.getMapper(PassengerMapper.class);
    }

    protected PlaceMapper getPlaceMapper(SqlSession session) {
        return session.getMapper(PlaceMapper.class);
    }

    protected DatabaseCleanup getDataBaseCleanup(SqlSession session) {
        return session.getMapper(DatabaseCleanup.class);
    }

    protected SessionMapper getSessionMapper(SqlSession session) {
        return session.getMapper(SessionMapper.class);
    }

}
