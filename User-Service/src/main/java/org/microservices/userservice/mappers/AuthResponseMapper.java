
package org.microservices.userservice.mappers;

import org.mapstruct.Mapper;
import org.microservices.userservice.DTO.AuthResponseDto;
import org.microservices.userservice.entity.User;

/**
 * Interface for mapping between User entities and AuthResponseDto objects.
 * Utilizes MapStruct for simplifying the mapping logic.
 * This mapper is Spring-aware and integrated as a Spring component.
 */
@Mapper(componentModel = "spring")
public interface AuthResponseMapper {

    /**
     * Maps a User entity to an AuthResponseDto object.
     *
     * @param user The User entity to be converted, containing information such as email, full name,
     *             authentication provider, and role.
     * @return An AuthResponseDto representing authentication details such as the JWT token and token type.
     */
    AuthResponseDto userToAuthResponseDto(User user);
    /**
     * Maps an AuthResponseDto object to a User entity.
     *
     * @param authResponseDto The AuthResponseDto containing authentication details such as
     *                        a JWT token and token type.
     * @return A User entity constructed using the details from the provided AuthResponseDto.
     */
    User authResponseDtoToUser(AuthResponseDto authResponseDto);


    AuthResponseDto usetoAuthResponseDto(String token);
}