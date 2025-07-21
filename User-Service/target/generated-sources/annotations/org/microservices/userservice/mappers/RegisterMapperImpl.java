package org.microservices.userservice.mappers;

import javax.annotation.processing.Generated;
import org.microservices.userservice.DTO.RegisterRequestDto;
import org.microservices.userservice.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-21T12:28:00+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class RegisterMapperImpl implements RegisterMapper {

    @Override
    public User registerRequestDtoToUser(RegisterRequestDto registerRequestDto) {
        if ( registerRequestDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( registerRequestDto.getEmail() );
        user.fullName( registerRequestDto.getFullName() );
        user.password( registerRequestDto.getPassword() );

        return user.build();
    }

    @Override
    public RegisterRequestDto userToRegisterRequestDto(User user) {
        if ( user == null ) {
            return null;
        }

        RegisterRequestDto registerRequestDto = new RegisterRequestDto();

        registerRequestDto.setEmail( user.getEmail() );
        registerRequestDto.setPassword( user.getPassword() );
        registerRequestDto.setFullName( user.getFullName() );

        return registerRequestDto;
    }
}
