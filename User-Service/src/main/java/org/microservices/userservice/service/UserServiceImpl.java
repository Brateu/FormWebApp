package org.microservices.userservice.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.microservices.userservice.DTO.AuthResponseDto;
import org.microservices.userservice.DTO.LoginRequestDto;
import org.microservices.userservice.DTO.RegisterRequestDto;
import org.microservices.userservice.DTO.UserDto;
import org.microservices.userservice.entity.User;
import org.microservices.userservice.enums.AuthProvider;
import org.microservices.userservice.enums.Role;
import org.microservices.userservice.exceptions.UserServiceException;
import org.microservices.userservice.mappers.RegisterMapper;
import org.microservices.userservice.mappers.UserMapper;
import org.microservices.userservice.repository.UserRepository;
import org.microservices.userservice.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the {@link UserService} interface responsible for managing user authentication-related operations.
 * This service provides functionalities for user registration, login, and logout.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final RegisterMapper registerMapper = Mappers.getMapper(RegisterMapper.class);
  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  /**
   * Registers a new user in the system. If a user with the provided email already exists, an exception will be thrown.
   * The user's password will be encoded, and a JWT token will be generated upon successful registration.
   *
   * @param request the DTO containing the user's registration details, including email, password, and other user information.
   * @return an {@link AuthResponseDto} containing the generated JWT token for the newly registered user.
   * @throws UserServiceException if a user with the provided email already exists.
   */
  @Override
  public AuthResponseDto register(RegisterRequestDto request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserServiceException("Email already in use");
    }
    System.out.println("Request DTO fields: " + request.toString());

    User mappedUser = registerMapper.registerRequestDtoToUser(request);

    System.out.println("User object after mapping: " + mappedUser);

    User user = User.builder()
            .id(mappedUser.getId())
            .email(mappedUser.getEmail())
            .fullName(mappedUser.getFullName())
            .password(passwordEncoder.encode(request.getPassword()))
            .authProvider(AuthProvider.LOCAL)
            .role(Role.USER)
            .enabled(mappedUser.isEnabled())
            .build();

    System.out.println("User object before saving: " + user);

    user = userRepository.save(user);
    String token = jwtTokenProvider.generateToken(user);

    return AuthResponseDto.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .build();
  }

  /**
   * Authenticates a user based on the provided login request data and generates a JWT token if the credentials are valid.
   *
   * @param request the login request data containing user email and password
   * @return an AuthResponseDto containing the generated JWT token for the authenticated user
   * @throws UserServiceException if the user is not found or the credentials are invalid
   */
  @Override
  public AuthResponseDto login(LoginRequestDto request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new UserServiceException("User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new UserServiceException("Invalid credentials");
    }

    String token = jwtTokenProvider.generateToken(user);
    return AuthResponseDto.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .build();
  }

  /**
   * Retrieves the currently authenticated user from the security context.
   *
   * @param authentication the authentication object from the security context
   * @return the currently authenticated user's details
   * @throws UserServiceException if no authenticated user is found
   */
  @Override
  public UserDto getCurrentUser(Authentication authentication) {
    String email = authentication.getName();
    User user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UserServiceException("User not found"));

    return userMapper.userToUserDto(user);
  }

  /**
   * Authenticates a user using OAuth2 provider information.
   *
   * @param email the user's email address
   * @param name the user's full name
   * @param providerId the provider-specific user ID
   * @return authentication response containing a JWT token
   */
  @Override
  public AuthResponseDto authenticateOAuthUser(String email, String name, String providerId) {
    User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
              User newUser = User.builder()
                      .email(email)
                      .fullName(name)
                      .password(passwordEncoder.encode(providerId))
                      .authProvider(AuthProvider.GITHUB)
                      .providerId(providerId)
                      .role(Role.USER)
                      .enabled(true)
                      .build();

              return userRepository.save(newUser);
            });


    String token = jwtTokenProvider.generateToken(user);
    return AuthResponseDto.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .build();
  }

  /**
   * Logs out the currently authenticated user.
   * This invalidates the user's session and any associated tokens.
   *
   * @return true if logout was successful, false otherwise
   */
  @Override
  public boolean logout() {
    try {
      SecurityContextHolder.clearContext();
      log.info("User logged out successfully");
      return true;
    } catch (Exception e) {
      log.error("Error during logout: {}", e.getMessage());
      return false;
    }
  }
}