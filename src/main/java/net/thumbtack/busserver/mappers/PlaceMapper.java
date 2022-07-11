package net.thumbtack.busserver.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import net.thumbtack.busserver.model.Order;
import net.thumbtack.busserver.model.Place;

public interface PlaceMapper {

    @Insert("INSERT INTO places (orderId, place) VALUES (#{orderId}, #{place})")
    void insertPlaceBus(Place place);

    @Select("select orderId, tripId, date, passengerId, place from places join orders on places.orderId = orders.id where tripId = #{tripId} and date = #{date}")
    @Result(column = "place")
    List<Integer> getBusyPlaces(Order order);

    @Insert("INSERT INTO places (orderId, passengerId, place) VALUES (#{orderId}, #{passenger.id}, #{place})")
    boolean selectPlace(Place place);

    // @Select("SELECT EXISTS ( SELECT * FROM places WHERE passengerId = #{passengerId}) FOR UPDATE")
    // Integer checkPlace(Integer passengerId);

    @Delete("DELETE places FROM places join orders on places.orderId = orders.id WHERE tripId = #{order.tripId} AND date = #{order.date} AND passengerId = #{passengerId}")
    void removePlace(@Param("order") Order order, @Param("passengerId") Integer passengerId);

    @Select("SELECT amountPlace FROM trip_dates JOIN dates ON dates.id = trip_dates.dateId WHERE tripId = #{tripId} and dayName = #{date} FOR UPDATE")
    Integer getNumberOfFreePlaces(Order order);

    @Select("SELECT EXISTS ( SELECT * FROM places JOIN orders on places.orderId = orders.id WHERE tripId = #{order.tripId} AND date = #{order.date} AND place = #{place})")
    boolean checkPlace(@Param("order") Order order, @Param("place") Integer place);
    
    @Delete("DELETE FROM places WHERE orderId = #{orderId} AND passengerId = #{passengerId} AND place != #{place}")
    void removePlaceIfExist(Integer orderId, Integer passengerId, Integer place);

}
