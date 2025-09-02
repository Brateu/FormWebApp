package org.microservices.formservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservices.formservice.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Implementation of the UserService interface using WebClient to communicate with the User-Service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final WebClient userServiceWebClient;

    /**
     * Retrieves a user ID by email using WebClient to communicate with the User-Service.
     *
     * @param email the email of the user to look up
     * @return the user ID if found, or null if not found
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public Long getUserIdByEmail(String email) {
        log.info("Looking up user ID for email: {}", email);

        try {
            return userServiceWebClient.get()
                    .uri("/api/user/email/{email}/id", email)
                    .retrieve()
                    .bodyToMono(Long.class)
                    .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                        log.error("User not found with email: {}", email);
                        throw new UserNotFoundException(email);
                    })
                    .onErrorResume(ex -> {
                        log.error("Error looking up user with email: {}", email, ex);
                        throw new RuntimeException("Error looking up user with email: " + email, ex);
                    })
                    .block();
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error looking up user with email: {}", email, e);
            throw new RuntimeException("Error looking up user with email: " + email, e);
        }
    }
}