package net.thumbtack.busserver.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import net.thumbtack.busserver.model.Bus;

public interface BusMapper {

    @Select("SELECT * FROM buses")
    List<Bus> getAllBuses();

    @Select("SELECT * FROM buses WHERE busName = #{name}")
    Bus getBusByName(String name);

}