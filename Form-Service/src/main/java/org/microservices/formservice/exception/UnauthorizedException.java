package org.microservices.formservice.exception;

/**
 * Exception thrown when a user is not authorized to perform an operation.
 * This exception is used to indicate that the current user does not have
 * sufficient permissions to access or modify the requested resource.
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Constructs a new UnauthorizedException with the specified detail message.
     *
     * @param message the detail message
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnauthorizedException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}