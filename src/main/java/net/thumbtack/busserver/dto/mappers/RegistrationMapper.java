package net.thumbtack.busserver.dto.mappers;

import java.util.List;

import net.thumbtack.busserver.dto.request.RegistrationUpdateRequest;
import net.thumbtack.busserver.dto.response.UserResponse;
import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.User;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RegistrationMapper {
    Client registrationRequestToClient(RegistrationUpdateRequest registrationClient);
    Administration registrationRequestToAdministration(RegistrationUpdateRequest registrationClientRequest);

    @Mappings({ 
        @Mapping(target = "id", expression = "java(user.getId())"),
        @Mapping(target = "email", expression = "java(((Client) user).getEmail() )"),
        @Mapping(target = "numberPhone", expression = "java(((Client) user).getNumberPhone() )"),
        @Mapping(target = "userType", expression = "java(user.getRole().getName())")
    })
    UserResponse userToRegistrationClientResponse(User user);

    default UserResponse userToUserResponse(User user) {
        if ( user instanceof Client) {
            return clientToUserResponse((Client) user);
        } else if ( user instanceof Administration ) {
            return administrationToUserResponse((Administration) user);
        } else {
            return null;
        }
    }

    @Mappings({
        @Mapping(target = "id", expression = "java(client.getId())"),
        @Mapping(target = "userType", expression = "java(client.getRole().getName())")
    })
    UserResponse clientToUserResponse(Client client);

    @Mappings({
        @Mapping(target = "id", expression = "java(administration.getId())"),
        @Mapping(target = "userType", expression = "java(administration.getRole().getName())")
    })
    UserResponse administrationToUserResponse(Administration administration);

    List<UserResponse> listClientToUserResponse(List<Client> clients);


}