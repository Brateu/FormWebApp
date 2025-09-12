package org.microservices.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler is a centralized exception handling class for the application.
 *
 * It uses Spring's {@code @RestControllerAdvice} annotation to provide global exception handling
 * across all controller classes. This class defines methods to handle specific exceptions
 * and return appropriate HTTP responses to the client.
 *
 * Exception Handlers:
 * - {@code UserServiceException}: Handles authentication-related exceptions and returns
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
     * Handles {@code UserServiceException} exceptions and returns a {@code 400 Bad Request}
     * HTTP status along with the exception message as a response body.
     *
     * This method is used to provide meaningful responses to authentication-related errors,
     * ensuring that clients receive clear and concise error messages.
     *
     * @param ex the {@code UserServiceException} that was thrown
     * @return a {@code ResponseEntity} containing the HTTP status {@code 400 Bad Request}
     *         and the exception message in the response body
     */
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<Map<String, Object>> handleAuthServiceException(UserServiceException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
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
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * Handles UserNotFoundException specifically.
     * Returns a 404 Not Found status with a JSON error response.
     *
     * @param ex the UserNotFoundException that was thrown
     * @return a ResponseEntity containing the HTTP status 404 Not Found
     *         and a JSON error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * Handles validation errors from @Valid on @RequestBody DTOs.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");

        Map<String, List<String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        body.put("messages", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    /**
     * Handles validation errors for query params/path variables.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");

        Map<String, List<String>> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(
                        v -> v.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));
        body.put("messages", violations);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    /**
     * Handles binding errors (e.g., when payload cannot be bound to DTO).
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");

        Map<String, List<String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        body.put("messages", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    /**
     * Handles RuntimeException specifically for user not found cases.
     * Returns a 404 Not Found status with a JSON error response.
     *
     * @param ex the RuntimeException that was thrown
     * @return a ResponseEntity containing the HTTP status 404 Not Found
     *         and a JSON error response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("User not found")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

        return handleGenericException(ex);
    }
}