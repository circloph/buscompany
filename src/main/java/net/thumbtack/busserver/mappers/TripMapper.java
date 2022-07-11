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
import org.apache.ibatis.annotations.Update;

import net.thumbtack.busserver.model.Bus;
import net.thumbtack.busserver.model.CustomDate;
import net.thumbtack.busserver.model.Order;
import net.thumbtack.busserver.model.Parameters;
import net.thumbtack.busserver.model.Schedule;
import net.thumbtack.busserver.model.Trip;

public interface TripMapper {

    @Select("SELECT * FROM trips WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "bus", column = "busName", javaType = Bus.class, one = @One(select = "net.thumbtack.busserver.mappers.BusMapper.getBusByName")),
            @Result(property = "fromStation", column = "fromStation"),
            @Result(property = "toStation", column = "toStation"),
            @Result(property = "start", column = "start"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "price", column = "price"),
            @Result(property = "approved", column = "approved"),
            @Result(property = "schedule", column = "id", javaType = Schedule.class, one = @One(select = "net.thumbtack.busserver.mappers.ScheduleMapper.getScheduleByTripId")),
            @Result(property = "dates", column = "id", javaType = List.class, many = @Many(select = "net.thumbtack.busserver.mappers.DateMapper.getAllDateByTripId"))
    })
    Trip getTripById(Integer id);

    @Insert("INSERT INTO trips (userId, busName, fromStation, toStation, start, duration, price, approved) VALUES " +
            "(#{userId}, #{bus.busName}, #{fromStation}, #{toStation}, #{start}, #{duration}, #{price}, false)")
        @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertTrip(Trip trip);

    @Insert("INSERT INTO trip_dates (tripId, dateId, amountPlace) VALUES (#{trip.id}, #{date.id}, #{bus.placeCount})")
    void insertDatesToTrip(@Param("trip") Trip trip, @Param("date") CustomDate date, @Param("bus") Bus bus);

    @Update("UPDATE trips SET approved = true WHERE id = #{tripId}")
    void approveTrip(Integer tripId);

    @Update({"<script>",
                "UPDATE trips JOIN schedule ON trips.id = schedule.tripId SET busName = #{bus.busName}, fromStation = #{fromStation}, toStation = #{toStation}, start = #{start}, duration = #{duration}, price = #{price} ",
                        "<if test='schedule != null'>, fromDate = #{schedule.fromDate}, toDate = #{schedule.toDate}, period = #{schedule.period}",
                "</if>",
                        "WHERE trips.id = #{id}",
                "</script>" })
    void updateTrip(Trip trip);

    @Delete("DELETE FROM trips WHERE userId = #{userId}")
    void deleteTripByUserId(Integer userId);

        @Select({"<script>",
                "SELECT id, userId, busName, fromStation, toStation, start, duration, price, fromDate, toDate, period FROM trips JOIN schedule ON trips.id = schedule.tripId WHERE approved = true",
                        "<if test='toDate != null'> AND DATE(toDate) &lt; #{toDate}",
                "</if>",
                        "<if test='fromDate != null'> AND DATE(fromDate) > #{fromDate}",
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
                @Result(property = "userId", column = "userId"),
                @Result(property = "bus", column = "busName", javaType = Bus.class, one = @One(select = "net.thumbtack.busserver.mappers.BusMapper.getBusByName")),
                @Result(property = "schedule", column = "id", javaType = Schedule.class, one = @One(select = "net.thumbtack.busserver.mappers.ScheduleMapper.getScheduleByTripId")),
                @Result(property = "dates", column = "id", javaType = List.class, many = @Many(select = "net.thumbtack.busserver.mappers.DateMapper.getAllDateByTripId"))
        })
        List<Trip> getTripsForClient(Parameters parameters);



        @Select({"<script>",
                "SELECT * FROM trips JOIN schedule ON trips.id = schedule.tripId join trip_dates on trip_dates.tripId = trips.id join dates on trip_dates.dateId = dates.id",
                "<where>",  
                        "<if test='toDate != null'> AND DATE(dayName) &lt;= #{toDate}",
                        "</if>",
                                "<if test='fromDate != null'> AND DATE(dayName) >= #{fromDate}",
                        "</if>",
                                "<if test='busName != null'> AND busName = #{busName}",
                        "</if>",
                                "<if test='fromStation != null'> AND fromStation = #{fromStation}",
                        "</if>",
                                "<if test='toStation != null'> AND toStation = #{toStation}",
                        "</if>",
                "</where>", 
                "</script>"
         })
        @Results({
                @Result(property = "id", column = "id"),
                @Result(property = "approved", column = "approved"),
                @Result(property = "bus", column = "busName", javaType = Bus.class, one = @One(select = "net.thumbtack.busserver.mappers.BusMapper.getBusByName")),
                @Result(property = "schedule", column = "id", javaType = Schedule.class, one = @One(select = "net.thumbtack.busserver.mappers.ScheduleMapper.getScheduleByTripId")),
                @Result(property = "dates", column = "id", javaType = List.class, many = @Many(select = "net.thumbtack.busserver.mappers.DateMapper.getAllDateByTripId"))
        })
        List<Trip> getTripsForAdmin(Parameters parameters);


        @Update("UPDATE trip_dates JOIN dates ON dates.id = trip_dates.dateId SET amountPlace = amountPlace - #{amountPlacesInOrder} " +
        "WHERE tripId = #{order.tripId} AND dayName = #{order.date} AND amountPlace >= #{amountPlacesInOrder}")
        Integer checkAndUpdateNumberOfPlaces(@Param("amountPlacesInOrder") Integer amountPlacesInOrder, @Param("order") Order order);

}
