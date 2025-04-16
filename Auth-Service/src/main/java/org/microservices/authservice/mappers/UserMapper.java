package org.microservices.authservice.mappers;


import org.mapstruct.Mapper;
import org.microservices.authservice.DTO.UserDto;
import org.microservices.authservice.entity.User;


/**
 * Interface for mapping between User entities and UserDto objects.
 * Utilizes MapStruct for simplifying the mapping logic.
 * This mapper is Spring-aware and is available as a Spring component.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a User entity to a UserDto object.
     * This method is used to convert the details of a User entity into a simplified data transfer object (UserDto)
     * for use in communication between different layers of the application, such as APIs and services.
     *
     * @param user The User entity to be converted, containing user properties such as id, email, full name,
     *             and other related information mapped from the database.
     * @return A UserDto object containing a subset of user information from the provided User entity,
     *         including id, email, and full name.
     */
    UserDto userToUserDto(User user);
    /**
     * Maps a UserDto object to a User entity.
     * This method is used to convert the details from a simplified user data transfer object (UserDto)
     * into a User entity for use in database operations or business logic.
     *
     * @param userDto The UserDto object containing user information such as id, email, and full name.
     * @return A User entity populated with the corresponding fields from the provided UserDto.
     */
    User userDtoToUser(UserDto userDto);


}