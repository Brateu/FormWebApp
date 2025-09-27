# Response Service

## Overview
The Response Service manages form responses, including creating drafts, submitting responses, searching, exporting/importing CSV data, and providing analytics for forms.

## Features
- Create/update/delete responses
- Draft and submit workflow
- Pagination, sorting, and search by question answer
- Export/import responses as CSV
- Analytics: statistics, time series, completion rate, average response time

## API Endpoints

Base path: `/api/responses`

- `POST /api/responses` Create a new response
- `PUT /api/responses/{id}` Update an existing response
- `GET /api/responses/{id}` Get a response by ID
- `DELETE /api/responses/{id}` Delete a response
- `GET /api/responses/form/{formId}` List responses for a form with optional filters:
  - query params: `status`, `startDate`, `endDate`, `page`, `size`, `sort`, `direction`
- `GET /api/responses/user/{userId}` List responses by user with pagination
- `GET /api/responses/search` Search responses by `formId`, optional `questionId`, `answer` with pagination
- `POST /api/responses/draft` Save a draft response
- `GET /api/responses/draft/latest?formId={formId}&userId={userId}` Get latest draft for form and user
- `POST /api/responses/{id}/submit` Submit a draft response
- `GET /api/responses/export/csv/{formId}` Export responses to CSV
- `POST /api/responses/import/csv/{formId}` Import responses from CSV; body is CSV text

Analytics base path: `/api/analytics`
- `GET /api/analytics/statistics/{formId}` Response statistics map
- `GET /api/analytics/time-series/{formId}?startDate=ISO&endDate=ISO&interval=day|hour|week|month` Time series data
- `GET /api/analytics/completion-rate/{formId}` Completion rate metrics
- `GET /api/analytics/response-time/{formId}` Average response time metrics

## Security
- Expects JWT authentication via API Gateway; user identity may be propagated with headers (e.g., X-User-ID)
- Service validates access to responses based on user and form ownership rules

## Environment Variables
Typical configuration via Spring Boot properties (application.yml or environment vars):
- `SPRING_DATASOURCE_URL` JDBC URL for the response database
- `SPRING_DATASOURCE_USERNAME` Database user
- `SPRING_DATASOURCE_PASSWORD` Database password
- `SPRING_JPA_HIBERNATE_DDL_AUTO` Schema strategy (e.g., update)
- `SERVER_PORT` Port to run the service (default 8080 when standalone; behind gateway via docker-compose)

If using JWT validation in this service (optional):
- `SECURITY_JWT_SECRET` Secret for token validation

## Running Locally
- With Maven: `mvn spring-boot:run` inside `Response-Service`
- Ensure database is reachable and env vars are set

## Docker/Compose
This service participates in the root `docker-compose.yml`. Ensure you have environment variables set or provided via compose overrides if necessary.

## Technologies
- Spring Boot
- Spring Web
- Spring Data JPA
- Lombok
- MapStruct
