package org.microservices.formservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a standardized error response for API errors.
 * This class is used to provide consistent error information to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * The HTTP status code of the response.
     */
    private int status;

    /**
     * A short, human-readable summary of the error.
     */
    private String error;

    /**
     * A more detailed message explaining the error.
     */
    private String message;

    /**
     * The path of the request that caused the error.
     */
    private String path;

    /**
     * The timestamp when the error occurred.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Creates a new ErrorResponse with the current timestamp.
     *
     * @param status the HTTP status code
     * @param error the error type
     * @param message the error message
     * @param path the request path
     * @return a new ErrorResponse
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}