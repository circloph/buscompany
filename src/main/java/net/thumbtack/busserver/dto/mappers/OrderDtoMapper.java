package net.thumbtack.busserver.dto.mappers;

import java.util.List;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import net.thumbtack.busserver.dto.request.OrderRequest;
import net.thumbtack.busserver.dto.request.PlaceRequest;
import net.thumbtack.busserver.dto.response.OrderResponse;
import net.thumbtack.busserver.dto.response.PlaceResponse;
import net.thumbtack.busserver.model.Order;
import net.thumbtack.busserver.model.Passenger;
import net.thumbtack.busserver.model.Place;
import net.thumbtack.busserver.model.Trip;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tripId", source = "request.tripId")
    @Mapping(target = "bus", expression = "java(trip.getBus())")
    Order orderRequestToOrder(OrderRequest request, Trip trip);

    @Mappings({
        @Mapping(target = "orderId", source = "id"),
        @Mapping(target = "busName", expression = "java(order.getBus().getBusName())"),
        @Mapping(target = "totalPrice", expression = "java(order.getPassengers().size() * order.getPrice())")
    })
    OrderResponse orderToOrderResponse(Order order);

    List<OrderResponse> listOrderToOrderResponse(List<Order> orders);

    Place placeRequestAndPassengerToPlace(PlaceRequest request, Passenger passenger);


    @Mapping(target = "ticket", source = "ticket")
    @Mapping(target = "firstName", expression = "java(place.getPassenger().getFirstName())")
    @Mapping(target = "lastName", expression = "java(place.getPassenger().getLastName())")
    @Mapping(target = "passport", expression = "java(place.getPassenger().getPassport())")
    PlaceResponse toPlaceResponse(Place place, String ticket);


}
