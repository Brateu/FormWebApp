package org.microservices.authservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler is a centralized exception handling class for the application.
 *
 * It uses Spring's {@code @RestControllerAdvice} annotation to provide global exception handling
 * across all controller classes. This class defines methods to handle specific exceptions
 * and return appropriate HTTP responses to the client.
 *
 * Exception Handlers:
 * - {@code AuthServiceException}: Handles authentication-related exceptions and returns
 *   a {@code 400 Bad Request} status along with the exception message.
 * - {@code Exception}: Handles all other uncaught exceptions and returns
 *   a {@code 500 Internal Server Error} status with a generic error message.
 *
 * This approach promotes cleaner and more maintainable error handling and ensures consistent
 * responses in case of exceptions occurring within the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@code AuthServiceException} exceptions and returns a {@code 400 Bad Request}
     * HTTP status along with the exception message as a response body.
     *
     * This method is used to provide meaningful responses to authentication-related errors,
     * ensuring that clients receive clear and concise error messages.
     *
     * @param ex the {@code AuthServiceException} that was thrown
     * @return a {@code ResponseEntity} containing the HTTP status {@code 400 Bad Request}
     *         and the exception message in the response body
     */
    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<String> handleAuthServiceException(AuthServiceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    /**
     * Handles all uncaught exceptions and returns a {@code 500 Internal Server Error}
     * HTTP status with a generic error message.
     *
     * This method is a fallback to handle unexpected exceptions that are not explicitly
     * handled by other methods in the application, ensuring an appropriate response
     * is sent to the client.
     *
     * @param ex the {@code Exception} that was thrown
     * @return a {@code ResponseEntity} containing the HTTP status {@code 500 Internal Server Error}
     *         and a generic error message in the response body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
}