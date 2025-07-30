package org.microservices.userservice.mappers;

import javax.annotation.processing.Generated;
import org.microservices.userservice.DTO.AuthResponseDto;
import org.microservices.userservice.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-30T16:02:53+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class AuthResponseMapperImpl implements AuthResponseMapper {

    @Override
    public AuthResponseDto userToAuthResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        AuthResponseDto.AuthResponseDtoBuilder authResponseDto = AuthResponseDto.builder();

        return authResponseDto.build();
    }

    @Override
    public User authResponseDtoToUser(AuthResponseDto authResponseDto) {
        if ( authResponseDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        return user.build();
    }

    @Override
    public AuthResponseDto usetoAuthResponseDto(String token) {
        if ( token == null ) {
            return null;
        }

        AuthResponseDto.AuthResponseDtoBuilder authResponseDto = AuthResponseDto.builder();

        authResponseDto.token( token );

        return authResponseDto.build();
    }
}
