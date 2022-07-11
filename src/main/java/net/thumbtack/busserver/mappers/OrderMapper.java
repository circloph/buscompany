package net.thumbtack.busserver.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import net.thumbtack.busserver.model.Bus;
import net.thumbtack.busserver.model.Order;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Passenger;

public interface OrderMapper {

    @Insert("INSERT INTO orders (tripId, userId, date) VALUES (#{tripId}, #{userId}, #{date})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOrder(Order order);

    @Select("SELECT orders.id, tripId, orders.userId, date, busName, fromStation, toStation, start, duration, price FROM orders JOIN trips on trips.id = tripId WHERE orders.id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "bus", column = "busName", javaType = Bus.class, one = @One(select = "net.thumbtack.busserver.mappers.BusMapper.getBusByName")),
        @Result(property = "passengers", column = "id", javaType = List.class,
        many = @Many(select = "net.thumbtack.busserver.mappers.PassengerMapper.getPassengerByOrderId"))
    })
    Order getOrderById(Integer id);

    @Insert("INSERT INTO orders_passengers (orderId, passengerId) VALUES (#{order.id}, #{passenger.id})")
    void insertOrderIdAndPassengerId(@Param("order") Order order, @Param("passenger") Passenger passenger);

        @Delete("DELETE FROM orders WHERE userId = #{userId}")
        void deleteOrderByUserId(Integer userId);

        @Select({"<script>",
        "SELECT orders.id, tripId, orders.userId, date, busName, fromStation, toStation, start, duration, price FROM orders JOIN trips on trips.id = tripId WHERE approved = true",
                "<if test='userId != null'> AND orders.userId = #{userId}",
        "</if>",    
                "<if test='toDate != null'> AND DATE(date) &lt;= #{toDate}",
        "</if>",
                "<if test='fromDate != null'> AND DATE(date) >= #{fromDate}",
        "</if>",
                "<if test='busName != null'> AND busName = #{busName}",
        "</if>",
                "<if test='fromStation != null'> AND fromStation = #{fromStation}",
        "</if>",
                "<if test='toStation != null'> AND toStation = #{toStation}",
        "</if>",
        "</script>" })
        @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "orders.userId"),
        @Result(property = "fromStation", column = "fromStation"),
        @Result(property = "toStation", column = "toStation"),
        @Result(property = "price", column = "price"),
        @Result(property = "start", column = "start"),
        @Result(property = "duration", column = "duration"),
        @Result(property = "toStation", column = "toStation"),
        @Result(property = "bus", column = "busName", javaType = Bus.class, one = @One(select = "net.thumbtack.busserver.mappers.BusMapper.getBusByName")),
        @Result(property = "passengers", column = "id", javaType = List.class,
        many = @Many(select = "net.thumbtack.busserver.mappers.PassengerMapper.getPassengerByOrderId"))
        })
        List<Order> getListOrderForAll(Parameters parameters);

}
