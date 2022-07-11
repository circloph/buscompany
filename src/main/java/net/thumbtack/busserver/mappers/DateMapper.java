package net.thumbtack.busserver.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import net.thumbtack.busserver.model.CustomDate;

public interface DateMapper {

    @Select("SELECT * FROM dates WHERE id IN (SELECT dateId FROM trip_dates WHERE tripId = #{id})")
    List<CustomDate> getAllDateByTripId(Integer id);

    @Insert("INSERT INTO dates (dayName) VALUES (#{dayName})")
    @Options(useGeneratedKeys = true, keyProperty = "id",  keyColumn = "id")
    void insertDate(CustomDate date);

    @Select("SELECT * FROM dates JOIN trip_dates ON dates.id = trip_dates.dateId WHERE dayName = #{dayName} AND tripId = #{tripId}")
    CustomDate getDateByDayName(String dayName, Integer tripId);

    @Delete("DELETE dates FROM dates JOIN trip_dates ON trip_dates.dateId = dates.id WHERE tripId = 1")
    void deleteDates(Integer tripId);
    
}
