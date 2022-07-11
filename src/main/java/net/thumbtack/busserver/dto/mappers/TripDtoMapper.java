package net.thumbtack.busserver.dto.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import net.thumbtack.busserver.dto.request.TripRequest;
import net.thumbtack.busserver.dto.response.BusResponse;
import net.thumbtack.busserver.dto.response.TripResponse;
import net.thumbtack.busserver.model.Bus;
import net.thumbtack.busserver.model.CustomDate;
import net.thumbtack.busserver.model.Trip;

@Mapper(componentModel = "spring", imports = {CustomDate.class, Collectors.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TripDtoMapper {

    @Mapping(target = "dates", expression = "java(stringMapToDate(requestTrip.getDates()))")
    Trip tripRequestToTrip(TripRequest requestTrip);

    @Mapping(target = "dates", expression = "java(dateMapToString(trip.getDates()))")
    @Mapping(target = "tripId", source = "id")
    TripResponse tripToTripResponse(Trip trip);

    @Mapping(target = "dates", expression = "java(dateMapToString(trip.getDates()))")
    @Mapping(target = "tripId", source = "id")
    List<TripResponse> tripToTripResponse(List<Trip> trip);

    default List<CustomDate> stringMapToDate(List<String> dates) {
        if (dates != null) {
            return dates.stream().distinct().map(date -> new CustomDate(date)).collect(Collectors.toList());
        }
        return null;
    }

    default List<String> dateMapToString(List<CustomDate> dates) {
        if (dates != null) {
            return dates.stream().map(date -> date.getDayName()).collect(Collectors.toList());
        }
        return null;
    }

    void updateTripFromRequest(@MappingTarget Trip trip, TripRequest request);

    BusResponse busToBusResponse(Bus bus);

    List<BusResponse> listBusToListResponse(List<Bus> buses);

}
