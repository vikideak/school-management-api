# School Management API

This project is a Spring Boot REST API for managing schools and students. It supports CRUD operations, searching, and asynchronous enrollment of students into schools.

---

## Features

- Create, read, update, delete (CRUD) operations for **Schools** and **Students**.
- Search schools/students with pagination and filtering.
- Asynchronous student enrollment into schools with capacity validation.
- Exception handling with error messages.
- Dockerized for easy deployment.

---

## Prerequisites

- Java 21 (only for local test run)
- Gradle 8.6 (only for local test run)
- Docker & Docker Compose
- Optional: IDE (IntelliJ, VSCode) for development

---

## Configuration

### Running the project

1. Build Docker images and start containers:
   run `docker compose up -d`

   - The API will be available at http://localhost:8080/swagger-ui/index.html.
   - Postgres database will run in a container as defined in docker-compose.yml.

2. Stop containers:
run `docker compose down -v`

### Running the tests
Run `./gradlew test`

---

## API Endpoints

Schools

- POST /schools – Create a school
- GET /schools – Search schools with pagination
- GET /schools/{id} – Get school details
- PUT /schools/{id} – Update a school
- DELETE /schools/{id} – Delete a school

Students

- POST /students – Create a student
- GET /students – Search students with pagination
- GET /students/{id} – Get student details
- PUT /students/{id} – Update a student
- DELETE /students/{id} – Delete a student

Enrollments

- POST /enrollments – Enroll a student asynchronously
- GET /enrollments/{id} – Check enrollment status

---

## Notes

- Database migrations are managed by Flyway (src/main/resources/db/migration).
- The asynchronous enrollment process runs every 5 seconds to simulate processing delays.
