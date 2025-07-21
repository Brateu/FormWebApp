package org.microservices.userservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.microservices.userservice.DTO.RegisterRequestDto;
import org.microservices.userservice.entity.User;

/**
 * Interface for mapping between RegisterRequestDto and User entities.
 * Utilizes MapStruct to simplify the mapping logic between DTOs and entities.
 * This mapper is Spring-aware, making it available as a Spring component.
 */
@Mapper(componentModel = "spring")
public interface RegisterMapper {
    /**
     * Maps a RegisterRequestDto object to a User entity.
     *
     * @param registerRequestDto The data transfer object containing registration details such as
     *                           email, password, and full name.
     * @return A User entity populated with the corresponding fields from the provided
     *         registerRequestDto, including email and full name.
     */
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "fullName")

    User registerRequestDtoToUser(RegisterRequestDto registerRequestDto);
    /**
     * Maps a User entity to a RegisterRequestDto object.
     * This method is used to convert user details, including email,
     * password, and full name, from the User entity format into a
     * RegisterRequestDto format.
     *
     * @param user The User entity to be converted, containing fields
     *             such as email, password, full name, authentication
     *             provider, provider ID, and role.
     * @return A RegisterRequestDto object containing the user’s registration
     *         details, including email, password, and full name,
     *         extracted from the provided User entity.
     */
    RegisterRequestDto userToRegisterRequestDto(User user);
}