package org.microservices.authservice.mappers;

import javax.annotation.processing.Generated;
import org.microservices.authservice.DTO.AuthResponseDto;
import org.microservices.authservice.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-16T13:40:36+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class AuthResponseMapperImpl implements AuthResponseMapper {

    @Override
    public AuthResponseDto userToAuthResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        AuthResponseDto authResponseDto = new AuthResponseDto();

        return authResponseDto;
    }

    @Override
    public User authResponseDtoToUser(AuthResponseDto authResponseDto) {
        if ( authResponseDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        return user.build();
    }
}
