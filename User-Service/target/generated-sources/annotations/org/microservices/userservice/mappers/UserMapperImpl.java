package org.microservices.userservice.mappers;

import javax.annotation.processing.Generated;
import org.microservices.userservice.DTO.UserDto;
import org.microservices.userservice.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-30T16:02:53+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setId( user.getId() );
        userDto.setEmail( user.getEmail() );
        userDto.setFullName( user.getFullName() );
        userDto.setAuthProvider( user.getAuthProvider() );

        return userDto;
    }

    @Override
    public User userDtoToUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userDto.getId() );
        user.email( userDto.getEmail() );
        user.fullName( userDto.getFullName() );
        user.authProvider( userDto.getAuthProvider() );

        return user.build();
    }
}
