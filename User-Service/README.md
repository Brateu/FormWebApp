# User Service API Documentation

## Overview
The User Service provides authentication and user management functionality for the application. It supports three authentication methods:
1. Local (username/password)
2. Google OAuth2
3. GitHub OAuth2

## API Endpoints

### Authentication

#### Register a new user
```
POST /api/user/register
```
Request body:
```json
{
  "email": "user@example.com",
  "password": "password",
  "fullName": "John Doe"
}
```
Response:
```json
{
  "token": "JWT_TOKEN",
  "tokenType": "Bearer"
}
```

#### Login with local credentials
```
POST /api/user/login
```
Request body:
```json
{
  "email": "user@example.com",
  "password": "password"
}
```
Response:
```json
{
  "token": "JWT_TOKEN",
  "tokenType": "Bearer"
}
```

#### OAuth2 Login (Google and GitHub)
The application supports OAuth2 login with Google and GitHub. The frontend should redirect users to the appropriate OAuth2 provider URL:

- Google: `/oauth2/authorization/google`
- GitHub: `/oauth2/authorization/github`

After successful authentication, the OAuth2 provider will redirect back to the application, and the user will receive a JWT token.

#### Logout
```
POST /api/user/logout
```
Response:
```
"Logged out successfully"
```

### User Information

#### Get current user details
```
GET /api/user/details
```
Headers:
```
Authorization: Bearer JWT_TOKEN
```
Response:
```json
{
  "id": 1,
  "email": "user@example.com",
  "fullName": "John Doe",
  "authProvider": "LOCAL"
}
```

## Authentication Flow

### Local Authentication
1. User registers or logs in with email and password
2. Server validates credentials and returns a JWT token
3. Client includes the JWT token in the Authorization header for subsequent requests

### OAuth2 Authentication (Google/GitHub)
1. User is redirected to the OAuth2 provider's login page
2. After successful authentication, the provider redirects back to the application
3. Server creates or updates the user account and returns a JWT token
4. Client includes the JWT token in the Authorization header for subsequent requests

## Security
- All endpoints except `/api/user/register`, `/api/user/login`, and OAuth2 endpoints require authentication
- JWT tokens expire after 1 hour (configurable in application.yml)
- Passwords are securely hashed using BCrypt
- **Note**: This service is only responsible for generating JWT tokens. Token validation can be handled by either:
  1. The API Gateway microservice (recommended)
  2. The User-Service itself (fallback)

### Authentication Flow Options

#### Option 1: API Gateway Authentication (Recommended)
In this configuration, the API Gateway validates JWT tokens and forwards user information to the User-Service:

1. Client sends a request with a JWT token to the API Gateway
2. API Gateway validates the token and extracts user information
3. API Gateway adds user information as headers (X-User-Email, X-User-ID) to the request
4. API Gateway forwards the request to the User-Service
5. User-Service extracts user information from the headers and creates an Authentication object
6. User-Service processes the request using the Authentication object

This approach centralizes authentication logic in the API Gateway, simplifying the microservices.

#### Option 2: Service-Level Authentication (Fallback)
If the API Gateway doesn't provide user information headers, the User-Service falls back to validating JWT tokens itself:

1. Client sends a request with a JWT token to the User-Service (directly or via API Gateway)
2. User-Service extracts and validates the JWT token
3. User-Service creates an Authentication object based on the token
4. User-Service processes the request using the Authentication object

This approach provides redundancy in case the API Gateway configuration changes.