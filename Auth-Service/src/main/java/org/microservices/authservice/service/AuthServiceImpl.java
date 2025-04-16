package org.microservices.authservice.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.microservices.authservice.DTO.AuthResponseDto;
import org.microservices.authservice.DTO.LoginRequestDto;
import org.microservices.authservice.DTO.RegisterRequestDto;
import org.microservices.authservice.entity.User;
import org.microservices.authservice.enums.AuthProvider;
import org.microservices.authservice.enums.Role;
import org.microservices.authservice.exceptions.AuthServiceException;
import org.microservices.authservice.mappers.AuthResponseMapper;
import org.microservices.authservice.mappers.LoginMapper;
import org.microservices.authservice.mappers.RegisterMapper;
import org.microservices.authservice.mappers.UserMapper;
import org.microservices.authservice.repository.UserRepository;
import org.microservices.authservice.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.security.Principal;

/**
 * Implementation of the {@link AuthService} interface responsible for managing user authentication-related operations.
 * This service provides functionalities for user registration, login, and retrieving the currently authenticated user.
 * It utilizes various mappers and external components to process requests and generate the necessary responses.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RegisterMapper registerMapper = Mappers.getMapper(RegisterMapper.class);
    private final LoginMapper loginMapper = Mappers.getMapper(LoginMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final AuthResponseMapper authResponseMapper = Mappers.getMapper(AuthResponseMapper.class);



    /**
     * Registers a new user in the system. If a user with the provided email already exists, an exception will be thrown.
     * The user's password will be encoded, and a JWT token will be generated upon successful registration.
     *
     * @param request the DTO containing the user's registration details, including email, password, and other user information.
     * @return an {@link AuthResponseDto} containing the generated JWT token for the newly registered user.
     * @throws AuthServiceException if a user with the provided email already exists.
     */
    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthServiceException("Email already in use");
        }
        System.out.println("Request DTO fields: " + request.toString());

        User user = registerMapper.registerRequestDtoToUser(request);

        System.out.println("User object after mapping: " + user);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setRole(Role.ROLE_USER);

        System.out.println("User object before saving: " + user);

        userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user);

        return new AuthResponseDto(token);
    }



    /**
     * Authenticates a user based on the provided login request data and generates a JWT token if the credentials are valid.
     *
     * @param request the login request data containing user email and password
     * @return an AuthResponseDto containing the generated JWT token for the authenticated user
     * @throws AuthServiceException if the user is not found or the credentials are invalid
     */
    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthServiceException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthServiceException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponseDto(token);
    }


    /**
     * Retrieves the current authenticated user and returns an authentication response containing a JWT token.
     *
     * @param principal the security principal representing the authenticated user. This could be of type {@code UserDetails}, {@code OAuth2User},
     *                  or {@code Authentication}, depending on the authentication provider.
     * @return an {@code AuthResponseDto} containing the generated JWT token for the authenticated user.
     * @throws AuthServiceException if the {@code principal} type is unsupported or if the user cannot be found.
     */
    @Override
    public AuthResponseDto getCurrentUser(Principal principal) {
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof Authentication) {
            email =  ((Authentication)principal).getName();
        }

        else {
            throw new AuthServiceException("Unsupported Principal type");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthServiceException("User not found"));

        return new AuthResponseDto(jwtTokenProvider.generateToken(user));
    }


}