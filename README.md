# FormApp Microservices

## Overview
FormApp is a modern, microservices-based application for creating, managing, and sharing forms. It provides a comprehensive solution for form creation, user management, and secure API access. The application is built using a microservices architecture, with each service responsible for a specific domain of functionality.

## Architecture
The application consists of four main microservices:

1. **API Gateway**: Serves as the entry point for all client requests, handles authentication, and routes requests to the appropriate microservices.
2. **User Service**: Manages user accounts, authentication, and authorization.
3. **Form Service**: Handles form creation, management, questions, options, and collaboration.
4. **Response Service**: Manages form responses, drafts, submissions, exports/imports, and analytics.

### System Architecture Diagram
```
                   ┌───────────────────┐
                   │       Client      │
                   │  (Web/Mobile/UI)  │
                   └─────────┬─────────┘
                             │ HTTPS + JWT
                    ┌────────▼──────────┐
                    │    API Gateway    │
                    │ (Auth, Routing)   │
                    └──┬──────┬──────┬──┘
                       │      │      │  Reactive Routing
        ┌──────────────▼┐  ┌──▼───────────┐  ┌───────────────▼ ┐
        │  User Service │  │  Form Service│  │ Response Service│
        │ (Auth, Users) │  │ (Forms, Q&A) │  │ (Responses, ANA)│
        └───────┬───────┘  └────┬──────── ┘  └────────┬────────┘
                │               │                     │
        ┌───────▼───────┐  ┌────▼─────────┐    ┌──────▼────────── ┐
        │ User Database │  │ Form Database│    │ Response Storage │
        └───────────────┘  └──────────────┘    └──────────────────┘

Notes:
- Client communicates only with API Gateway; Gateway validates JWT and forwards requests.
- Gateway fan-outs to User/Form/Response services; services persist to their own stores.
- Optional inter-service calls (e.g., Form -> User) go via HTTP/Feign and are omitted for clarity.
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

### Response Service
The Response Service manages responses to forms, including drafts, submissions, search, CSV import/export, and analytics.

Key responsibilities:
- Create, update, delete responses
- Draft and submit workflow
- Search and pagination
- CSV export/import
- Analytics (statistics, time series, completion rate)

[More details](./Response-Service/README.md)

## Environment Variables Setup

This project uses environment variables to store sensitive information such as database credentials, OAuth2 client secrets, and JWT configuration. This approach enhances security by keeping sensitive information out of the codebase.

### Setting Up Environment Variables

Each service has its own `.env` file that contains the necessary environment variables. Before running the services, make sure these environment variables are properly set in your environment.

> **Note for Windows Users**: When running Docker Compose on Windows, you need to run the Docker client with elevated privileges. Right-click on the Docker Desktop icon and select "Run as administrator". If you don't run Docker with elevated privileges, you'll see an error like: `error during connect: in the default daemon configuration on Windows, the docker client must be run with elevated privileges to connect`.

#### API Gateway

The API Gateway requires the following environment variables:

```
# JWT Configuration
SECURITY_JWT_SECRET=your-jwt-secret
SECURITY_JWT_TOKEN_EXPIRATION=3600000
```

#### User Service

The User Service requires the following environment variables:

```
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/userdb
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
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/formdb
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
```

#### Response Service

The Response Service requires the following environment variables (MongoDB):

```
# MongoDB Configuration
RESPONSE_DB_HOST=localhost
RESPONSE_DB_PORT=27017
RESPONSE_DB_NAME=responses
RESPONSE_DB_USERNAME=your-mongo-username
RESPONSE_DB_PASSWORD=your-mongo-password

# Optional JWT validation in service (when not delegated to Gateway)
SECURITY_JWT_SECRET=your-jwt-secret
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

5. **Testing the Databases**:
   - Test PostgreSQL connections using the psql command-line tool:
     ```bash
     # Test User-Service database connection
     psql -h localhost -p 5432 -U ${USER_DB_USERNAME:-postgres} -d userdb -c "SELECT 1"

     # Test Form-Service database connection
     psql -h localhost -p 5433 -U ${FORM_DB_USERNAME:-postgres} -d formdb -c "SELECT 1"
     ```
   - Test MongoDB (Response-Service) using mongosh or MongoDB Compass:
     ```bash
     # With mongosh
     mongosh --host localhost --port 27017 -u ${RESPONSE_DB_USERNAME} -p ${RESPONSE_DB_PASSWORD} --authenticationDatabase admin <<'EOF'
     use responses
     db.runCommand({ ping: 1 })
     EOF
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

## Docker Setup

This project is containerized using Docker, making it easy to set up and run in any environment.

### Prerequisites

- [Docker](https://www.docker.com/get-started) installed on your machine
- [Docker Compose](https://docs.docker.com/compose/install/) installed on your machine

### Configuration

1. **Environment Variables**:
   - The project includes a `.env` file in the root directory with default values
   - Update the values in this file with your actual credentials before running the application
   - Each service also has its own `.env` file for service-specific configuration

2. **Docker Compose**:
   - The `docker-compose.yml` file in the root directory orchestrates all services
   - It defines the services, networks, and volumes needed for the application

### Running the Application

1. **Build and start all services**:
   ```bash
   docker-compose up -d
   ```

2. **View logs**:
   ```bash
   docker-compose logs -f
   ```

3. **Stop all services**:
   ```bash
   docker-compose down
   ```

4. **Rebuild services after making changes**:
   ```bash
   docker-compose up -d --build
   ```

### Service URLs

Once the application is running, you can access the services at:

- API Gateway: http://localhost:8080
- User Service: http://localhost:8070
- Form Service: http://localhost:8090
- Response Service: http://localhost:8060

### Database Access

The databases are exposed on the following ports:

- User PostgreSQL: localhost:5432 (DB name: userdb)
- Form PostgreSQL: localhost:5433 (DB name: formdb)
- Response MongoDB: localhost:27017 (DB name: responses)

You can connect using clients like pgAdmin/DBeaver (PostgreSQL) and MongoDB Compass/mongo shell (MongoDB).

### Security Considerations

- Never commit actual secrets to version control
- Use different secrets for development, testing, and production environments
- Regularly rotate secrets
- Consider using a secret management service like HashiCorp Vault or AWS Secrets Manager for production environments
# FormApp - Microservices Application

This is a microservices-based application for creating and managing forms.

## Services

- **User-Service**: Handles user authentication and management
- **Form-Service**: Manages form creation and editing
- **Response-Service**: Collects and processes form responses
- **API-Gateway**: Routes requests to the appropriate services

## Prerequisites

- Docker and Docker Compose
- Java 17 or higher

## Running the Application

1. Make sure Docker is running on your machine
2. Clone this repository
3. Navigate to the project root directory
4. Start the databases using Docker Compose:

```bash
docker-compose up -d
```

5. Start each service individually:

```bash
# Start User-Service
cd User-Service
./mvnw spring-boot:run

# Start Form-Service
cd Form-Service
./mvnw spring-boot:run

# Start Response-Service
cd Response-Service
./mvnw spring-boot:run

# Start API-Gateway
cd API-Gateway
./mvnw spring-boot:run
```

## Environment Variables

The application uses environment variables for configuration. These are loaded from a `.env` file in the project root directory. See `.env.example` for the required variables.

## API Documentation

- User-Service: http://localhost:8070/swagger-ui.html
- Form-Service: http://localhost:8090/swagger-ui.html
- Response-Service: http://localhost:8060/swagger-ui.html
- API-Gateway: http://localhost:8080