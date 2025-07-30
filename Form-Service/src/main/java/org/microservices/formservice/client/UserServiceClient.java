package org.microservices.formservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the User Service.
 */
@FeignClient(name = "user-service", url = "${user-service.url}", configuration = org.microservices.formservice.config.FeignConfig.class)
public interface UserServiceClient {

    /**
     * Get a user ID by email using path variable.
     *
     * @param email The email of the user
     * @return ResponseEntity containing the user ID
     */
    @GetMapping("/api/user/email/{email}/id")
    ResponseEntity<Long> getUserIdByEmailResponse(@PathVariable("email") String email);

    /**
     * Convenience method to get a user ID by email.
     * This method unwraps the ResponseEntity and returns just the ID.
     *
     * @param email The email of the user
     * @return The user ID or null if not found
     */
    default Long getUserIdByEmail(String email) {
        try {
            ResponseEntity<Long> response = getUserIdByEmailResponse(email);
            return response.getBody();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().equals("Received HTML error page")) {
                throw new org.microservices.formservice.exception.UserNotFoundException(email);
            }
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }
}