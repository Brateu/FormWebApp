package org.microservices.responseservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The {@code ApiError} class represents a standardized structure for error
 * responses in an API. It is typically used to encapsulate error information
 * when an exception occurs within the API. This class contains details about
 * the error, such as the HTTP status, message, and additional information.
 *
 * Features:
 * - Includes a timestamp indicating when the error occurred.
 * - Contains an HTTP status code and its associated reason phrase.
 * - Provides a detailed error message and additional details, if applicable.
 * - Tracks the request path where the error occurred.
 *
 * This class is commonly utilized within exception handling to create
 * meaningful error responses for the client.
 *
 * An instance of this class is usually built using its builder, allowing
 * dynamic population of error fields based on the context.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;
}
