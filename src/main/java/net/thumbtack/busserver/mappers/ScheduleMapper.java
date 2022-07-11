package net.thumbtack.busserver.mappers;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import net.thumbtack.busserver.model.Schedule;

public interface ScheduleMapper {

    @Select("SELECT * FROM schedule WHERE tripId = #{id}")
    Schedule getScheduleByTripId(Integer id);

    @Insert("INSERT INTO schedule (tripId, fromDate, toDate, period) VALUES (#{tripId}, #{schedule.fromDate}, #{schedule.toDate}, #{schedule.period})")
    void insertSchedule(@Param("tripId") Integer tripId, @Param("schedule") Schedule schedule);

    @Delete("DELETE from schedule WHERE tripId = #{tripId}")
    void deleteSchedule(Integer tripId);
    
}
