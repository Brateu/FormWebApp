package org.microservices.userservice.mappers;

import org.mapstruct.Mapper;
import org.microservices.userservice.DTO.LoginRequestDto;
import org.microservices.userservice.entity.User;

/**
 * Interface for mapping between LoginRequestDto objects and User entities.
 * Utilizes MapStruct for simplifying the mapping logic.
 * This mapper is Spring-aware and integrated as a Spring component.
 */
@Mapper(componentModel = "spring")
public interface LoginMapper {

    /**
     * Maps a LoginRequestDto object to a User entity.
     *
     * @param loginRequestDto The data transfer object containing login credentials, including
     *                        email and password.
     * @return A User entity built from the provided loginRequestDto, including fields such as
     *         email and password.
     */
    User loginRequestDtoToUser(LoginRequestDto loginRequestDto);
    /**
     * Converts a User entity to a LoginRequestDto object.
     * This method is typically used to map user details, including email and password,
     * from the User entity into a LoginRequestDto format required for login-related processes.
     *
     * @param user The User entity to be converted. It contains fields such as email, password,
     *             full name, authentication provider, and role.
     * @return A LoginRequestDto object containing the email and password extracted from the
     *         provided User entity.
     */
    LoginRequestDto userToLoginRequestDto(User user);
}