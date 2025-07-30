# API Gateway

## Overview
The API Gateway serves as the entry point for all client requests in the Form Application ecosystem. It routes requests to the appropriate microservices, handles cross-cutting concerns such as authentication, authorization, and provides a unified interface for clients to interact with the backend services.

## Features
- Request routing to appropriate microservices
- JWT-based authentication and authorization
- User identity propagation to downstream services
- Rate limiting and circuit breaking
- Request/response transformation
- Centralized security management

## Architecture
The API Gateway is built using Spring Cloud Gateway and follows a reactive programming model:
- **Routing Layer**: Defines routes to different microservices
- **Filter Layer**: Applies pre and post-filters to requests and responses
- **Security Layer**: Handles authentication and authorization

## Key Components

### Routing
- Dynamic route configuration based on service discovery
- Path-based routing to different microservices
- Load balancing across service instances

### Security
- JWT token validation
- Role-based access control
- User identity extraction and propagation

### Filters
- **UserIdFilter**: Extracts user ID from JWT tokens and adds it as a header
- **UserIdPropagationFilter**: Propagates user identity to downstream services

## Routes
The API Gateway routes requests to the following microservices:
- **User Service**: Handles user management and authentication
  - Path: `/api/user/**`
- **Form Service**: Manages forms, questions, and collaborators
  - Path: `/api/forms/**`

## Security
- All requests except public endpoints require a valid JWT token
- JWT tokens are validated using the shared secret key
- User roles are extracted from tokens and used for authorization decisions
- User ID is propagated to downstream services via the X-User-ID header

## Configuration
The API Gateway can be configured through the following properties:
- `jwt.secret`: Secret key for JWT token validation
- `spring.cloud.gateway.routes`: Route definitions
- `spring.security.oauth2.resourceserver.jwt`: JWT resource server configuration

## Technologies
- Spring Boot
- Spring Cloud Gateway
- Spring Security
- Spring WebFlux
- JWT (JSON Web Tokens)
- Reactor (Reactive Streams)