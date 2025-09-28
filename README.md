# FormWebApp

During development on the QA branch, the application was tested using a combination of manual testing, API testing, and end-to-end (E2E) testing:

**API Testing (Apidog):**
All backend service endpoints (User Service, Form Service, Response Service, and API Gateway) were validated using Apidog. Test cases covered authentication, form creation, collaborators, responses, and export functionality.
The requests and responses were verified to ensure correct status codes, payload structures, and error handling.

**E2E Testing (Playwright):**
Automated UI tests were written in Playwright and executed against the frontend connected to the running backend.
The following flows were covered:

- User registration and login (positive and negative cases)

- Form creation, editing, and saving

- Adding collaborators and verifying permissions

- Viewing responses and exporting results

**Manual Testing:**
In addition to automated tests, the application was manually tested through the browser to validate critical user journeys, including:

- Authentication and session handling

- Form CRUD operations

- Collaborator management

- Response submission and export features

**Result:**
All major features have been verified both through automation and manual QA. The application is stable and ready for further integration or deployment steps.
