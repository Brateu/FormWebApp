# FormApp Microservices

## Overview
FormApp is a modern, microservices-based application for creating, managing, and sharing forms. It provides a comprehensive solution for form creation, user management, and secure API access. The application is built using a microservices architecture, with each service responsible for a specific domain of functionality.

## Architecture
The application consists of three main microservices:

1. **API Gateway**: Serves as the entry point for all client requests, handles authentication, and routes requests to the appropriate microservices.
2. **User Service**: Manages user accounts, authentication, and authorization.
3. **Form Service**: Handles form creation, management, questions, options, and collaboration.

### System Architecture Diagram
```
┌─────────────┐      ┌─────────────┐
│             │      │             │
│   Client    │─────▶│ API Gateway │
│             │◀─────│             │
└─────────────┘      └──────┬──────┘
                           │
                           │
                ┌──────────┴──────────┐
                │                     │
        ┌───────▼────────┐   ┌────────▼───────┐
        │                │   │                │
        │  User Service  │   │  Form Service  │
        │                │   │                │
        └───────┬────────┘   └────────┬───────┘
                │                     │
                │                     │
        ┌───────▼────────┐   ┌────────▼───────┐
        │                │   │                │
        │  User Database │   │ Form Database  │
        │                │   │                │
        └────────────────┘   └────────────────┘
```

### Communication Flow
1. Clients send requests to the API Gateway
2. API Gateway authenticates requests and extracts user information
3. API Gateway routes requests to the appropriate microservice
4. Microservices process requests and return responses
5. API Gateway forwards responses back to clients

### Key Features
- **Authentication**: JWT-based authentication with support for username/password and OAuth2 (Google, GitHub)
- **Form Management**: Create, update, delete, and share forms with various question types
- **Collaboration**: Add collaborators to forms with different permission levels
- **Security**: Role-based access control and secure communication between services

## Microservices Details

### API Gateway
The API Gateway serves as the entry point for all client requests, handling authentication, authorization, and request routing.

Key responsibilities:
- Route requests to appropriate microservices
- Validate JWT tokens
- Extract and propagate user identity
- Apply security policies

[More details](./API-Gateway/README.md)

### User Service
The User Service manages user accounts, authentication, and authorization.

Key responsibilities:
- User registration and login
- OAuth2 authentication with Google and GitHub
- JWT token generation
- User profile management

[More details](./User-Service/README.md)

### Form Service
The Form Service handles form creation, management, questions, options, and collaboration.

Key responsibilities:
- Form CRUD operations
- Question and option management
- Form sharing and collaboration
- Form visibility and status control

[More details](./Form-Service/README.md)

## Environment Variables Setup

This project uses environment variables to store sensitive information such as database credentials, OAuth2 client secrets, and JWT configuration. This approach enhances security by keeping sensitive information out of the codebase.

### Setting Up Environment Variables

Each service has its own `.env` file that contains the necessary environment variables. Before running the services, make sure these environment variables are properly set in your environment.

#### API Gateway

The API Gateway requires the following environment variables:

```
# OAuth2 Client Credentials
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=your-google-client-id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=your-google-client-secret
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID=your-github-client-id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET=your-github-client-secret
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_USER_SERVICE_CLIENT_ID=api-gateway
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_USER_SERVICE_CLIENT_SECRET=api-gateway-secret

# JWT Configuration
SECURITY_JWT_SECRET=your-jwt-secret
SECURITY_JWT_TOKEN_EXPIRATION=3600000
```

#### User Service

The User Service requires the following environment variables:

```
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/User
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password

# OAuth2 Client Credentials
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=your-google-client-id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=your-google-client-secret
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID=your-github-client-id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET=your-github-client-secret

# JWT Configuration
SECURITY_JWT_SECRET=your-jwt-secret
SECURITY_JWT_TOKEN_EXPIRATION=3600000
```

#### Form Service

The Form Service requires the following environment variables:

```
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
```

### PostgreSQL Database Setup

This project uses PostgreSQL as the database for all services. Follow these steps to set up PostgreSQL:

1. **Install PostgreSQL**:
   - Download and install PostgreSQL from the [official website](https://www.postgresql.org/download/)
   - During installation, set a password for the default 'postgres' user
   - The default port is 5432, which is used in the connection URLs

2. **Create Required Databases**:
   - Open pgAdmin (comes with PostgreSQL) or use the psql command-line tool
   - Create the databases needed by the services:
     ```sql
     CREATE DATABASE "User";
     -- The 'postgres' database is created by default
     ```

3. **Configure PostgreSQL Connection Properties**:
   - The following properties can be customized in the environment variables:
     ```
     # Basic Connection Properties
     SPRING_DATASOURCE_URL=jdbc:postgresql://hostname:port/database
     SPRING_DATASOURCE_USERNAME=username
     SPRING_DATASOURCE_PASSWORD=password

     # Optional Advanced Properties
     # Add these to application.yml if needed
     SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=10
     SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5
     SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT=20000
     ```

4. **PostgreSQL Dialect Configuration**:
   - The services are already configured to use the PostgreSQL dialect:
     ```yaml
     spring:
       jpa:
         properties:
           hibernate:
             dialect: org.hibernate.dialect.PostgreSQLDialect
     ```

5. **Testing the PostgreSQL Connection**:
   - You can test your PostgreSQL connection using the psql command-line tool:
     ```bash
     # Test User-Service database connection
     psql -h localhost -p 5432 -U postgres -d User -c "SELECT 1"

     # Test Form-Service database connection
     psql -h localhost -p 5432 -U postgres -d postgres -c "SELECT 1"
     ```
   - If the connection is successful, you should see a result like:
     ```
      ?column?
     ----------
              1
     (1 row)
     ```
   - Alternatively, you can use a GUI tool like pgAdmin to test the connection

### Loading Environment Variables

There are several ways to load environment variables:

1. **Using .env files with a library like dotenv**:
   - Add the dotenv dependency to your project
   - Load the .env file at application startup

2. **Setting environment variables in your development environment**:
   - For Windows: Use the `set` command or System Properties
   - For macOS/Linux: Use the `export` command

3. **Setting environment variables in your deployment environment**:
   - For Docker: Use the `-e` flag or a docker-compose.yml file
   - For Kubernetes: Use ConfigMaps and Secrets
   - For cloud platforms: Use their environment variable configuration options

### Security Considerations

- Never commit actual secrets to version control
- Use different secrets for development, testing, and production environments
- Regularly rotate secrets
- Consider using a secret management service like HashiCorp Vault or AWS Secrets Manager for production environments