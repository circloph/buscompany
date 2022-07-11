package net.thumbtack.busserver.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import net.thumbtack.busserver.model.Passenger;

public interface PassengerMapper {

    @Insert("INSERT INTO passengers (firstName, lastName, passport) VALUES (#{firstName}, #{lastName}, #{passport})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertPassenger(Passenger passenger);

    @Select("SELECT * FROM passengers WHERE id IN (SELECT passengerId FROM orders_passengers WHERE orderId = #{orderId})")
    List<Passenger> getPassengerByOrderId(Integer orderId);

    @Select("SELECT * FROM passengers WHERE firstName = #{firstName} AND lastName = #{lastName} AND passport = #{passport}")
    @Result(property = "id", column = "id")
    Passenger getPassenger(Passenger passenger);
    
}
