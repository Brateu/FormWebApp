package org.microservices.authservice.service;

import lombok.RequiredArgsConstructor;
import org.microservices.authservice.entity.User;
import org.microservices.authservice.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * This service class implements the {@link UserDetailsService} interface,
 * enabling integration with Spring Security for user authentication and authorization.
 * It retrieves user details from the underlying data source for authentication purposes.
 *
 * This class relies on the {@link UserRepository} to fetch the user data from the database
 * using the provided email address.
 *
 * Responsibilities:
 * - Implements the {@code loadUserByUsername} method, fetching user details from the repository
 *   based on the provided email.
 * - Throws {@link UsernameNotFoundException} if no user is found for the given email.
 * - Constructs and returns a Spring Security {@link org.springframework.security.core.userdetails.User}
 *   for authentication with necessary details such as email, password, and granted authorities
 *   (derived from the user's role).
 *
 * Dependencies:
 * - {@link UserRepository}: To query user details from the database.
 *
 * Annotations:
 * - {@code @Service}: Marks this class as a Spring service component for dependency injection.
 * - {@code @RequiredArgsConstructor}: Automatically generates a constructor for initializing
 *   the final fields of the class, simplifying dependency injection.
 */
@Service
@RequiredArgsConstructor
public class CustomerUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by their email address and returns a {@link UserDetails} object
     * for authentication and authorization purposes.
     *
     * @param email the email address of the user to retrieve
     * @return a {@link UserDetails} object representing the user, including email, password,
     *         and granted authorities based on the user's role
     * @throws UsernameNotFoundException if no user is found with the provided email address
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String role = user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}