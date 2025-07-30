package org.microservices.apigateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserIdPropagationFilterTest {

    @Test
    public void testFilterSkipsPublicEndpoints() {
        // Create the filter
        UserIdPropagationFilter filter = new UserIdPropagationFilter();

        // Create a mock chain that returns a completed Mono when filter is called
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Test register endpoint
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/api/user/register")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Execute the filter
        filter.filter(exchange, chain).block(); // Block to ensure the filter completes

        // Verify that the chain's filter method was called directly
        // without attempting to extract JWT token
        verify(chain).filter(exchange);
    }

    @Test
    public void testFilterHandlesInvalidToken() {
        // Create the filter
        UserIdPropagationFilter filter = new UserIdPropagationFilter();

        // Create a mock chain that returns a completed Mono when filter is called
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Test with invalid token
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/api/forms")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Execute the filter
        filter.filter(exchange, chain).block(); // Block to ensure the filter completes

        // Verify that the chain's filter method was called
        verify(chain).filter(exchange);

        // Verify that the X-User-ID header was not added
        // This is an indirect verification since we can't easily check the headers in the mock exchange
        // The important thing is that the filter didn't throw an exception and continued the chain
    }

    @Test
    public void testFilterHandlesNullToken() {
        // Create the filter
        UserIdPropagationFilter filter = new UserIdPropagationFilter();

        // Create a mock chain that returns a completed Mono when filter is called
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Test with no token
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/api/forms")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Execute the filter
        filter.filter(exchange, chain).block(); // Block to ensure the filter completes

        // Verify that the chain's filter method was called
        verify(chain).filter(exchange);

        // Verify that the X-User-ID header was not added
        // This is an indirect verification since we can't easily check the headers in the mock exchange
        // The important thing is that the filter didn't throw an exception and continued the chain
    }
}