# Form Service

## Overview
The Form Service is a microservice responsible for managing forms, questions, options, and collaborators within the Form Application ecosystem. It provides a comprehensive API for creating, updating, and managing forms with various question types, as well as handling form sharing and collaboration.

## Features
- Form creation and management
- Question management with multiple question types
- Option management for multiple-choice questions
- Form collaboration with different permission levels
- Form visibility control (public/private)
- Form status management (draft/published/closed)

## Architecture
The Form Service follows a layered architecture:
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic and orchestrates operations
- **Repository Layer**: Manages data access and persistence
- **DTO Layer**: Provides data transfer objects for API communication
- **Entity Layer**: Defines the domain model and relationships

## Key Components

### Form Management
- Create, read, update, and delete forms
- Control form visibility (public/private)
- Manage form status (draft/published/closed)
- Lock/unlock forms to prevent/allow editing

### Question Management
- Support for various question types (text, multiple-choice, etc.)
- Question ordering within forms
- Question cloning functionality

### Option Management
- Create, read, update, and delete options for questions
- Associate options with specific questions

### Collaboration
- Add collaborators to forms with different permission levels (viewer/editor)
- Manage collaborator access to forms

## API Endpoints

### Form Endpoints
- `GET /api/forms`: Get all forms
- `GET /api/forms/{id}`: Get a specific form by ID
- `POST /api/forms`: Create a new form
- `PUT /api/forms/{id}`: Update an existing form
- `DELETE /api/forms/{id}`: Delete a form
- `GET /api/forms/user`: Get forms for the current user
- `PUT /api/forms/{id}/status`: Update form status
- `PUT /api/forms/{id}/visibility`: Update form visibility
- `GET /api/forms/status/{status}`: Get forms by status
- `GET /api/forms/visibility/{visibility}`: Get forms by visibility
- `GET /api/forms/public`: Get all public forms
- `GET /api/forms/public/{id}`: Get a specific public form
- `POST /api/forms/{id}/copy`: Create a copy of a form
- `PUT /api/forms/{id}/lock`: Lock a form
- `PUT /api/forms/{id}/unlock`: Unlock a form

### Question Endpoints
- `GET /api/forms/{formId}/questions`: Get all questions for a form
- `GET /api/forms/{formId}/questions/{questionId}`: Get a specific question
- `POST /api/forms/{formId}/questions`: Create a new question
- `PUT /api/forms/{formId}/questions/{questionId}`: Update a question
- `DELETE /api/forms/{formId}/questions/{questionId}`: Delete a question
- `POST /api/forms/{formId}/questions/{questionId}/clone`: Clone a question
- `PUT /api/forms/{formId}/questions/reorder`: Reorder questions in a form

### Option Endpoints
- `GET /api/forms/{formId}/questions/{questionId}/options`: Get options for a question
- `POST /api/forms/{formId}/questions/{questionId}/options`: Create a new option
- `PUT /api/forms/{formId}/questions/{questionId}/options/{optionId}`: Update an option
- `DELETE /api/forms/{formId}/questions/{questionId}/options/{optionId}`: Delete an option

### Collaborator Endpoints
- `GET /api/forms/{formId}/collaborators`: Get collaborators for a form
- `POST /api/forms/{formId}/collaborators`: Add a collaborator to a form
- `PUT /api/forms/{formId}/collaborators/{collaboratorId}`: Update a collaborator
- `DELETE /api/forms/{formId}/collaborators/{collaboratorId}`: Remove a collaborator

## Security
- All endpoints require authentication except public form endpoints
- User ID is extracted from JWT tokens and used for authorization
- Form owners have full access to their forms
- Collaborators have access based on their assigned role (viewer/editor)

## Integration
- Communicates with the User Service to validate user existence and retrieve user information
- Uses Feign clients for service-to-service communication

## Technologies
- Spring Boot
- Spring Data JPA
- Spring Cloud OpenFeign
- PostgreSQL
- Lombok
- MapStruct