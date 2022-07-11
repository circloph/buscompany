package net.thumbtack.busserver.dto.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import net.thumbtack.busserver.dto.request.RegistrationUpdateRequest;
import net.thumbtack.busserver.dto.response.UserResponse;
import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UpdateUserMapper {

    @Mappings({
        @Mapping(target = "email" , source = "email"),
        @Mapping(target = "numberPhone" , source = "numberPhone"),
        @Mapping(target = "password", source = "newPassword")
    })
    void updateClientFromRequest(@MappingTarget Client client, RegistrationUpdateRequest request);

    @Mappings({
        @Mapping(target = "password", source = "newPassword")
    })
    void updateAdministrationFromRequest(@MappingTarget Administration administration, RegistrationUpdateRequest request);

    @Mapping(target = "userType", expression = "java(administration.getRole().getName())")
    @Mapping(target = "id", source = "id", ignore = true)
    UserResponse adminToUpdateUserResponse(Administration administration);

    
    @Mapping(target = "userType", expression = "java(client.getRole().getName())")
    @Mapping(target = "id", source = "id", ignore = true)
    UserResponse clientToUpdateUserResponse(Client client);

    default UserResponse userToUpdateUserResponse(User user) {
        if ( user instanceof Client) {
            return clientToUpdateUserResponse((Client) user);
        } else if ( user instanceof Administration ) {
            return adminToUpdateUserResponse((Administration) user);
        } else {
            return null;
        }
    }
}
