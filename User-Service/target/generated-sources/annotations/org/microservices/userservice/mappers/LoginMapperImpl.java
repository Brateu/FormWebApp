package org.microservices.userservice.mappers;

import javax.annotation.processing.Generated;
import org.microservices.userservice.DTO.LoginRequestDto;
import org.microservices.userservice.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-21T12:28:00+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class LoginMapperImpl implements LoginMapper {

    @Override
    public User loginRequestDtoToUser(LoginRequestDto loginRequestDto) {
        if ( loginRequestDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( loginRequestDto.getEmail() );
        user.password( loginRequestDto.getPassword() );

        return user.build();
    }

    @Override
    public LoginRequestDto userToLoginRequestDto(User user) {
        if ( user == null ) {
            return null;
        }

        LoginRequestDto loginRequestDto = new LoginRequestDto();

        loginRequestDto.setEmail( user.getEmail() );
        loginRequestDto.setPassword( user.getPassword() );

        return loginRequestDto;
    }
}
