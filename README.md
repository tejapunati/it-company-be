# IT Company MongoDB Backend

This is the backend for the IT Company Management System, built with Spring Boot and MongoDB.

## Prerequisites

- Java 17 or higher
- Maven
- MongoDB (running on localhost:27017)
- Docker and Docker Compose (optional, for running MongoDB in a container)

## Setup

1. Clone the repository
2. Start MongoDB:
   - Option 1: If you have MongoDB installed locally, make sure it's running on localhost:27017
   - Option 2: If you have Docker installed, run:
     ```
     docker-compose up -d
     ```
3. Build the project:
   ```
   ./mvnw clean install
   ```
4. Run the application:
   ```
   ./mvnw spring-boot:run
   ```
   
   Or on Windows:
   ```
   mvnw.cmd clean install
   mvnw.cmd spring-boot:run
   ```
   
   Or simply run the provided batch script:
   ```
   run.bat
   ```

## API Endpoints

The API is available at `http://localhost:8081/api/v1`

### Authentication

- `POST /auth/login` - Login with email and password
- `POST /auth/register` - Register a new user

### Users

- `GET /users` - Get all users (admin only)
- `GET /users/{id}` - Get user by ID
- `PUT /users/{id}` - Update user
- `POST /users/{id}/approve` - Approve user (admin only)
- `POST /users/{id}/reject` - Reject user (admin only)
- `DELETE /users/{id}` - Delete user (parent admin only)

### Timesheets

- `GET /timesheets` - Get all timesheets (admin only)
- `GET /timesheets/{id}` - Get timesheet by ID
- `GET /timesheets/user/{userId}` - Get timesheets by user ID
- `POST /timesheets` - Create a new timesheet
- `PUT /timesheets/{id}` - Update timesheet
- `POST /timesheets/{id}/approve` - Approve timesheet (admin only)
- `POST /timesheets/{id}/reject` - Reject timesheet (admin only)

### Email Logs

- `GET /email-logs` - Get all email logs (admin only)
- `GET /email-logs/{id}` - Get email log by ID (admin only)
- `GET /email-logs/user/{email}` - Get email logs by user email

## Default Users

The application comes with three default users:

1. Parent Admin:
   - Email: parent-admin@ssrmtech.com
   - Password: admin123
   - Role: PARENT_ADMIN

2. Regular Admin:
   - Email: admin@ssrmtech.com
   - Password: admin123
   - Role: ADMIN

3. Regular User:
   - Email: user@ssrmtech.com
   - Password: user123
   - Role: USER