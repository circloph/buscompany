package net.thumbtack.busserver.mappers;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;

public interface DatabaseCleanup {

    @Update("SET FOREIGN_KEY_CHECKS = 0")
    void disableForeignKeyChecking();

    @Update("SET FOREIGN_KEY_CHECKS = 1")
    void enableForeignKeyChecking();

    @Delete("TRUNCATE TABLE users")
    void clearUsers();

    @Delete("TRUNCATE TABLE administration")
    void clearAdministration();

    @Delete("TRUNCATE TABLE clients")
    void clearClients();

    @Delete("TRUNCATE TABLE trips")
    void clearTrips();

    @Delete("TRUNCATE TABLE schedule")
    void clearSchedules();

    @Delete("TRUNCATE TABLE dates")
    void clearDates();

    @Delete("TRUNCATE TABLE passengers")
    void clearPassengers();

    @Delete("TRUNCATE TABLE orders_passengers")
    void clearOrdersPassengers();

    @Delete("TRUNCATE TABLE orders")
    void clearOrders();
    
    @Delete("TRUNCATE TABLE trip_dates")
    void clearTripDates();

    @Delete("TRUNCATE TABLE places")
    void clearPlaces();
    
}
