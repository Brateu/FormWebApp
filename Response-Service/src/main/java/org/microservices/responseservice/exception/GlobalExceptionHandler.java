package org.microservices.responseservice.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A global exception handler for handling various types of exceptions across the application.
 * This class uses Spring's exception handling mechanisms to provide uniform and meaningful
 * error responses structured as {@code ApiError} objects. It handles exceptions such as validation
 * errors, bad requests, missing parameters, unsupported HTTP methods, and more, logging the errors
 * and constructing user-friendly error messages.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Constructs an {@code ApiError} object using the provided parameters. This method
     * encapsulates error details such as HTTP status, error message, request path,
     * and additional error-specific details in the form of a standardized error structure.
     *
     * @param status the HTTP status code representing the nature of the error
     * @param message a descriptive message providing further context about the error
     * @param path the URI or path of the request that triggered the error
     * @param details a list of additional details or specific points about the error,
     *                or {@code null} if no further details are available
     * @return an {@code ApiError} instance that contains the error information
     */
    private ApiError buildError(HttpStatus status, String message, String path, List<String> details) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .details(details == null || details.isEmpty() ? null : details)
                .build();
    }

    /**
     * Extracts the request path from the provided {@link WebRequest}. If the given
     * request is an instance of {@link ServletWebRequest}, this method retrieves the
     * request URI from the underlying {@code HttpServletRequest}.
     * Otherwise, it returns {@code null}.
     *
     * @param request the web request from which the path is to be extracted; may be an
     *                instance of {@link ServletWebRequest}
     * @return the request URI as a {@code String} if the input is a {@code ServletWebRequest};
     *         {@code null} otherwise
     */
    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest swr) {
            return swr.getRequest().getRequestURI();
        }
        return null;
    }

    /**
     * Handles exceptions related to bad requests, such as validation errors
     * and argument mismatches. This method processes exceptions of types
     * {@code IllegalArgumentException} and {@code MethodArgumentTypeMismatchException},
     * constructs a standardized {@code ApiError} object, and returns it as part
     * of the {@code ResponseEntity}.
     *
     * @param ex the exception that triggered this handler; may be an instance of
     *           {@code IllegalArgumentException} or {@code MethodArgumentTypeMismatchException}
     * @param request the web request instance associated with the error, used to extract
     *                request-specific details such as the path
     * @return a {@code ResponseEntity} wrapping an {@code ApiError} object that contains
     *         details about the error, along with an HTTP status of 400 (Bad Request)
     */
    // 400: Bad request for validation errors and argument issues
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex, WebRequest request) {
        log.warn("Bad request: {}", ex.getMessage());
        ApiError error = buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), extractPath(request), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles validation errors encountered during method argument validation in controller requests.
     * This method captures and processes {@link MethodArgumentNotValidException} exceptions,
     * extracting detailed error messages from invalid fields and returning a structured
     * {@code ApiError} response encapsulated in a {@code ResponseEntity}.
     *
     * @param ex the exception triggered due to invalid method arguments; contains details
     *           about validation errors, including the failing fields and their error messages
     * @param headers the HTTP headers sent along with the request
     * @param status the HTTP response status code indicating validation failure, typically {@code 400 Bad Request}
     * @param request the web request instance associated with this exception, used for extracting contextual details
     * @return a {@code ResponseEntity} object containing an {@code ApiError} with the details of the validation failure,
     *         such as the HTTP status, request path, error message, and a list of specific validation error details
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                details.add(fe.getField() + ": " + fe.getDefaultMessage()));
        ApiError error = buildError(HttpStatus.BAD_REQUEST, "Validation failed", extractPath(request), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles exceptions where the incoming HTTP request message is not readable,
     * typically due to malformed JSON. This method constructs an {@code ApiError}
     * object containing error details and returns it as a response with a 400 Bad Request status.
     *
     * @param ex the exception that triggered the error, typically a {@link HttpMessageNotReadableException}
     *           caused by invalid or poorly formatted request payloads
     * @param headers the HTTP headers included in the request
     * @param status the HTTP status code, indicating the type of error (400 Bad Request in this case)
     * @param request the web request in which the error occurred, used to extract details such as the request path
     * @return a {@link ResponseEntity} containing an {@code ApiError} object with structured error information
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ApiError error = buildError(HttpStatus.BAD_REQUEST, "Malformed JSON request", extractPath(request), List.of(ex.getMostSpecificCause().getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles cases where a required request parameter is missing in an HTTP request.
     * Constructs a standardized {@code ApiError} response with details about the missing parameter
     * and returns it as part of the exception handling process.
     *
     * @param ex the exception that triggered this handler, typically a {@link MissingServletRequestParameterException}
     *           providing details about the missing parameter
     * @param headers the HTTP headers included in the request
     * @param status the HTTP response status code, usually indicating a 400 Bad Request
     * @param request the web request where the exception occurred, used to extract contextual details such as the request path
     * @return a {@code ResponseEntity} containing an {@code ApiError} object that includes details about the error,
     *         along with an HTTP status of 400 (Bad Request)
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                         HttpHeaders headers,
                                                                         HttpStatusCode status,
                                                                         WebRequest request) {
        ApiError error = buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), extractPath(request), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles HTTP request method not supported exceptions by creating a standardized
     * {@code ApiError} object and returning it as part of the response.
     * This method processes {@code HttpRequestMethodNotSupportedException} errors
     * where the requested HTTP method is not allowed for the target resource.
     *
     * @param ex the exception triggered when an unsupported HTTP method is used for the request
     * @param headers the HTTP headers sent along with the request
     * @param status the HTTP response status code indicating the error type (405 Method Not Allowed)
     * @param request the web request where the exception occurred, used to extract the request path
     * @return a {@code ResponseEntity} containing an {@code ApiError} object with error details,
     *         including the HTTP status, message, and request path
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers,
                                                                         HttpStatusCode status,
                                                                         WebRequest request) {
        ApiError error = buildError(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), extractPath(request), null);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    /**
     * Handles exceptions of type {@link ConstraintViolationException} that typically occur
     * when one or more constraints on the model attributes are violated.
     *
     * This method extracts the specific violation messages and constructs a standardized
     * {@link ApiError} response. The response contains the HTTP status, a descriptive message,
     * the path where the error occurred, and detailed constraint violation messages.
     *
     * @param ex the exception that triggered this handler; contains details about
     *           the constraint violations
     * @param request the web request instance to extract additional context such
     *                as the URI path where the violation occurred
     * @return a {@code ResponseEntity} wrapping an {@code ApiError} object that contains
     *         details about the constraint violations, along with an HTTP status of 400 (Bad Request)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        ApiError error = buildError(HttpStatus.BAD_REQUEST, "Constraint violation", extractPath(request), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles `NoSuchElementException` exceptions that indicate a requested
     * resource was not found. This method constructs an `ApiError` object
     * containing the details of the error and returns it within a `ResponseEntity`
     * with a 404 Not Found HTTP status.
     *
     * @param ex the exception triggered when a requested resource is not found
     * @param request the web request from which the exception originated, used
     *                to extract contextual details such as the request path
     * @return a `ResponseEntity` wrapping an `ApiError` object that includes the
     *         HTTP status, error message, and request path
     */
    // 404: Not found
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(NoSuchElementException ex, WebRequest request) {
        ApiError error = buildError(HttpStatus.NOT_FOUND, ex.getMessage(), extractPath(request), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles exceptions of type {@link IllegalStateException} typically encountered
     * when the application is in an incorrect or unexpected state. This method
     * constructs a standardized {@link ApiError} response, providing details such as
     * the HTTP status, error message, and the request path where the exception occurred.
     *
     * @param ex the exception instance representing the illegal state encountered,
     *           containing a descriptive error message
     * @param request the web request associated with the exception, used for extracting
     *                the request-specific details such as the path
     * @return a {@code ResponseEntity} containing an {@link ApiError} object with the
     *         error details and an HTTP status of 400 (Bad Request)
     */
    // 409 / 400: state issues
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex, WebRequest request) {
        ApiError error = buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), extractPath(request), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles exceptions of type DataAccessException. This method logs the error,
     * constructs an ApiError object with details about the issue, and returns an
     * appropriate HTTP response.
     *
     * @param ex the DataAccessException that occurred during data access operations.
     * @param request the WebRequest instance containing details of the HTTP request
     *                that triggered the exception.
     * @return a ResponseEntity containing the ApiError object and an HTTP status of
     *         503 (Service Unavailable).
     */
    // 503/500 for data access layer issues
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDataAccess(DataAccessException ex, WebRequest request) {
        log.error("Data access error", ex);
        ApiError error = buildError(HttpStatus.SERVICE_UNAVAILABLE, "Database temporarily unavailable", extractPath(request), List.of(ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    /**
     * Handles exceptions triggered by missing required headers in a request.
     *
     * @param ex the exception thrown when a required request header is missing
     * @param request the web request context in which the exception occurred
     * @return a ResponseEntity containing an ApiError object and an HTTP status code of BAD_REQUEST
     */
    // Missing required header
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingHeader(MissingRequestHeaderException ex, WebRequest request) {
        ApiError error = buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), extractPath(request), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles all uncaught exceptions and returns a standardized error response.
     *
     * @param ex the exception that was not caught by other handlers
     * @param request the current web request during which the exception was thrown
     * @return a ResponseEntity containing an ApiError object with details about the error and an HTTP 500 status code
     */
    // Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        log.error("Unhandled exception", ex);
        ApiError error = buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", extractPath(request), List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
