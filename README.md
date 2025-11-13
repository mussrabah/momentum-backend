# Momentum Backend

This repository contains the official Spring Boot backend server for **Momentum**, a multi-platform (KMP) application designed for project management and focus.

This API provides secure, stateless JWT authentication and a full set of services for managing users, projects, tasks, and logging Pomodoro focus sessions.

## ✨ Features

* **Authentication**: Secure user registration and login (`/api/auth/register`, `/api/auth/login`) using Spring Security and JSON Web Tokens (JWT).
* **Projects**: Full CRUD (Create, Read, Update, Delete) functionality for projects. All endpoints are user-scoped.
* **Tasks**: Nested CRUD functionality for tasks, linked to projects. All endpoints are user-scoped and verify project ownership.
* **Pomodoro Logging**: Endpoints to log completed Pomodoro sessions, which can be linked to specific tasks.
* **Analytics**: A summary endpoint that provides user statistics like total focus time, tasks completed, and daily focus trends.
* **Database Migrations**: Uses Liquibase for safe, version-controlled database schema management.

## 🛠 Tech Stack

* **Framework**: [Spring Boot 3](https://spring.io/projects/spring-boot)
* **Language**: [Kotlin](https://kotlinlang.org/)
* **Database**: [PostgreSQL](https://www.postgresql.org/)
* **Schema Management**: [Liquibase](https://www.liquibase.org/)
* **Authentication**: [Spring Security](https://spring.io/projects/spring-security) + [JWT (jjwt)](https://github.com/jwtk/jjwt)
* **ORM**: Spring Data JPA / Hibernate
* **Build Tool**: [Gradle (Kotlin DSL)](https://gradle.org/)

## 🚀 Getting Started

### Prerequisites

* [Java 21 (or newer)](https://www.oracle.com/java/technologies/downloads/)
* [PostgreSQL](https://www.postgresql.org/download/)
* An API client like [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/) for testing.

### 1. Setup the Database

You must create a PostgreSQL database.

```sql
CREATE DATABASE momentum;
```

By default, the application tries to connect with the username `postgres`. You will need to set a password for this user.

### 2. Clone the Repository

```bash
git clone https://github.com/your-username/momentum-backend.git
cd momentum-backend
```

### 3. Configure Environment Variables

This application uses environment variables for sensitive data. The most reliable way to set these for development is in your IDE's "Run Configuration".

* `DB_URL`: The JDBC URL for your database.

  * *Default*: `jdbc:postgresql://localhost:5432/momentum`
* `DB_USERNAME`: The database user.

  * *Default*: `postgres`
* `DB_PASSWORD`: **(Required)** The password for your database user.
* `JWT_SECRET`: **(Required)** A long, random string used to sign JWTs. You can generate one [here](https://www.allkeysgenerator.com/Random/Security-Key-Generator).

### 4. Run the Application

Once your database is running and environment variables are set, you can run the app:

```bash
./gradlew bootRun
```

The server will start on `http://localhost:8080`. Liquibase will automatically run all migrations and create the necessary tables.

## 🔑 API Endpoints

All secured endpoints require an `Authorization: Bearer <token>` header.

### Authentication

* `POST /api/auth/register`

  * Registers a new user.
  * **Body**: `{ "name": "...", "email": "...", "password": "..." }`
  * **Returns**: `AuthResponse` (with tokens and user DTO)

* `POST /api/auth/login`

  * Logs in an existing user.
  * **Body**: `{ "email": "...", "password": "..." }`
  * **Returns**: `AuthResponse` (with tokens and user DTO)

### Projects

* `POST /api/projects` **(Secured)**

  * Creates a new project for the authenticated user.
  * **Body**: `CreateProjectRequest`
  * **Returns**: `ProjectResponse`

* `GET /api/projects` **(Secured)**

  * Gets all projects for the authenticated user.
  * **Returns**: `List<ProjectResponse>`

### Tasks

* `POST /api/projects/{projectId}/tasks` **(Secured)**

  * Creates a new task for a specific project.
  * Verifies that the user owns the project.
  * **Body**: `CreateTaskRequest`
  * **Returns**: `TaskResponse`

* `GET /api/projects/{projectId}/tasks` **(Secured)**

  * Gets all tasks for a specific project.
  * Verifies that the user owns the project.
  * **Returns**: `List<TaskResponse>`

### Pomodoro

* `POST /api/pomodoro/sessions` **(Secured)**

  * Logs a completed Pomodoro session for the user.
  * Verifies task ownership if `taskId` is provided.
  * **Body**: `LogPomodoroRequest`
  * **Returns**: `PomodoroSessionResponse`

### Analytics

* `GET /api/analytics/summary` **(Secured)**

  * Returns a full analytics report for the authenticated user.
  * **Returns**: `AnalyticsResponse`
