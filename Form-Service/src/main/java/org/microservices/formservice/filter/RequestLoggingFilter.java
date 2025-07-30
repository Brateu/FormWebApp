package org.microservices.formservice.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * A filter that intercepts incoming HTTP requests to log all the headers
 * present in the request. This filter is meant for debugging or logging
 * purposes to capture useful information regarding an HTTP request.
 *
 * This class is annotated with @Component, allowing it to be automatically
 * detected and registered as a Spring Bean during component scanning.
 *
 * Implements the {@link jakarta.servlet.Filter} interface to provide the standard
 * filter functionality.
 */
@Component
public class RequestLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    /**
     * Intercepts incoming HTTP requests to log all header names and their corresponding values.
     * Delegates request and response processing to the next filter in the filter chain after logging.
     * This method is part of the {@link jakarta.servlet.Filter} interface.
     *
     * @param request  the {@link ServletRequest} containing the client's request, which is cast
     *                 to {@link HttpServletRequest} to access HTTP-specific functionality.
     * @param response the {@link ServletResponse} containing the response to be sent back to the client.
     * @param chain    the {@link FilterChain} that passes the request and response to the
     *                 next filter in the chain for further processing.
     * @throws IOException      if an I/O error occurs during the filtering process.
     * @throws ServletException if an error occurs during the request processing.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        Collections.list(req.getHeaderNames()).forEach(headerName -> {
            log.info("Header '{}': {}", headerName, req.getHeader(headerName));
        });
        
        chain.doFilter(request, response);
    }
}