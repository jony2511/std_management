# Student Management System

[![CI Status](https://img.shields.io/badge/CI-GitHub%20Actions-blue)](../../actions)

A comprehensive web-based Student Management System built with **Spring Boot 4.0**, **Spring Security**, **Thymeleaf**, and **PostgreSQL**. This application provides role-based access control for managing students, teachers, courses, and departments.

## 📋 Table of Contents

- [CI/CD](#cicd)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Default Credentials](#default-credentials)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Security & Authorization](#security--authorization)
- [File Descriptions](#file-descriptions)

---

## 🔄 CI/CD

This project uses **GitHub Actions** for Continuous Integration. Every push and pull request automatically:
- ✅ Builds the project
- ✅ Runs all unit tests
- ✅ Generates test reports
- ✅ Validates code quality

**Workflow File**: [.github/workflows/ci.yml](.github/workflows/ci.yml)  
**Documentation**: See [CI_DOCUMENTATION.md](CI_DOCUMENTATION.md) for detailed explanation

### Build Status
Check the [Actions tab](../../actions) to see the latest build status and test results.

---

## ✨ Features

- **Role-Based Access Control**: Two roles - TEACHER and STUDENT with different permissions
- **Student Management**: Create, view, update, and delete student profiles
- **Teacher Management**: Manage teacher accounts and information
- **Course Management**: Add and manage courses with teacher assignments
- **Department Management**: Organize students, teachers, and courses by department
- **Student Self-Service**: Students can view and update their own profiles
- **Secure Authentication**: Login/logout functionality with password encryption
- **Responsive UI**: Clean Thymeleaf-based templates

---

## 🛠 Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming Language |
| Spring Boot | 4.0.2 | Application Framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | - | Database ORM |
| Thymeleaf | - | Server-side Template Engine |
| PostgreSQL | Latest | Relational Database |
| Lombok | - | Boilerplate Code Reduction |
| Maven | - | Build Tool |
| Docker Compose | - | Container Orchestration |

---

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

- **Java 17** or higher
- **Maven** (or use the included Maven Wrapper)
- **Docker & Docker Compose** (for running PostgreSQL)
- **Git** (optional, for cloning)

---

## 🚀 Getting Started

### Option 1: Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd demo
   ```

2. **Ensure Docker Desktop is running**
   - Open Docker Desktop application
   - Wait for it to fully start (check the system tray icon)

3. **Start the PostgreSQL database**
   ```bash
   docker compose up -d
   ```

4. **Run the application**
   ```bash
   # On Windows
   .\mvnw.cmd spring-boot:run

   # On Linux/Mac
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   Open your browser and navigate to: http://localhost:8080

### Option 2: Using Local PostgreSQL Installation

If you have PostgreSQL installed locally:

1. **Disable Docker Compose integration** in `src/main/resources/application.properties`:
   ```properties
   spring.docker.compose.enabled=false
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE student_management;
   CREATE USER myuser WITH PASSWORD 'secret';
   GRANT ALL PRIVILEGES ON DATABASE student_management TO myuser;
   ```

3. **Configure the database** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/student_management
   spring.datasource.username=myuser
   spring.datasource.password=secret
   ```

4. **Run the application**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

5. **Access the application**
   Open your browser and navigate to: http://localhost:8080

---

## ⚠️ Troubleshooting

### Application fails to start with "Docker" errors
If you see errors like `failed to connect to the docker API`:
- **Solution A**: Start Docker Desktop and wait for it to fully initialize
- **Solution B**: Disable Docker Compose in `application.properties`:
  ```properties
  spring.docker.compose.enabled=false
  ```

### Port 5432 already in use
If Docker Compose fails with "port is already allocated":
```bash
# Find the process using port 5432
netstat -ano | findstr :5432

# Stop the conflicting process or use a different port in compose.yaml
```

### Connection refused to PostgreSQL
If you see "Connection to localhost:5432 refused":
1. Ensure PostgreSQL is running: `docker ps` or check your local PostgreSQL service
2. Verify credentials in `application.properties` match your PostgreSQL setup

### Cannot find mvnw.cmd
Ensure you're in the correct directory:
```bash
cd E:\Project\student\demo
.\mvnw.cmd spring-boot:run
```

---

## 🔐 Default Credentials

On first startup, the application automatically creates:

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| Teacher/Admin | `admin` | `admin123` | Full access to all features |

> **Note**: Change the default password in production!

---

## 📁 Project Structure

```
demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── StudentManagementApplication.java    # Main entry point
│   │   │   ├── config/                              # Configuration classes
│   │   │   ├── controller/                          # HTTP request handlers
│   │   │   ├── dto/                                 # Data Transfer Objects
│   │   │   ├── entity/                              # JPA entities
│   │   │   ├── repository/                          # Data access layer
│   │   │   ├── security/                            # Security components
│   │   │   └── service/                             # Business logic layer
│   │   └── resources/
│   │       ├── application.properties               # App configuration
│   │       ├── static/                              # Static assets (CSS, JS)
│   │       └── templates/                           # Thymeleaf HTML templates
│   └── test/                                        # Unit & integration tests
├── compose.yaml                                     # Docker Compose config
├── pom.xml                                          # Maven dependencies
└── mvnw / mvnw.cmd                                  # Maven wrapper scripts
```

---

## 🌐 API Endpoints

### Public Endpoints
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/` | Home page |
| GET | `/login` | Login page |

### Dashboard (Authenticated)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/dashboard` | User dashboard |

### Student Endpoints
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| GET | `/students` | TEACHER, STUDENT | List all students |
| GET | `/students/view/{id}` | TEACHER, STUDENT | View student details |
| GET | `/students/create` | TEACHER | Show create form |
| POST | `/students/create` | TEACHER | Create new student |
| GET | `/students/edit/{id}` | TEACHER | Show edit form |
| POST | `/students/edit/{id}` | TEACHER | Update student |
| POST | `/students/delete/{id}` | TEACHER | Delete student |
| GET | `/students/profile` | STUDENT | View own profile |
| GET | `/students/profile/edit` | STUDENT | Edit own profile |

### Teacher Endpoints
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| GET | `/teachers` | TEACHER | List all teachers |
| GET | `/teachers/view/{id}` | TEACHER | View teacher details |
| GET | `/teachers/create` | TEACHER | Show create form |
| POST | `/teachers/create` | TEACHER | Create new teacher |
| GET | `/teachers/edit/{id}` | TEACHER | Show edit form |
| POST | `/teachers/edit/{id}` | TEACHER | Update teacher |
| POST | `/teachers/delete/{id}` | TEACHER | Delete teacher |

### Course Endpoints
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| GET | `/courses` | TEACHER, STUDENT | List all courses |
| GET | `/courses/view/{id}` | TEACHER, STUDENT | View course details |
| GET | `/courses/create` | TEACHER | Show create form |
| POST | `/courses/create` | TEACHER | Create new course |
| GET | `/courses/edit/{id}` | TEACHER | Show edit form |
| POST | `/courses/edit/{id}` | TEACHER | Update course |
| POST | `/courses/delete/{id}` | TEACHER | Delete course |

### Department Endpoints
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| GET | `/departments` | TEACHER | List all departments |
| GET | `/departments/create` | TEACHER | Show create form |
| POST | `/departments/create` | TEACHER | Create new department |
| GET | `/departments/edit/{id}` | TEACHER | Show edit form |
| POST | `/departments/edit/{id}` | TEACHER | Update department |
| POST | `/departments/delete/{id}` | TEACHER | Delete department |

---

## 🗄 Database Schema

### Entity Relationships

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     User        │       │   Department    │       │     Course      │
│ (Abstract Base) │       │     (Dept)      │       │                 │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id              │       │ id              │       │ id              │
│ username        │       │ name            │       │ name            │
│ password        │       │ description     │       │ code            │
│ email           │       │                 │       │ description     │
│ firstName       │       │ students[]    ◄─┼───────┼─ department     │
│ lastName        │       │ teachers[]    ◄─┼───┐   │ credits         │
│ phone           │       │ courses[]     ◄─┼───┼───┼─ teacher        │
│ role            │       └─────────────────┘   │   │ students[]      │
│ enabled         │                             │   └─────────────────┘
└────────┬────────┘                             │           ▲
         │                                      │           │
    ┌────┴────┐                                 │           │
    │         │                                 │           │
┌───▼───┐ ┌───▼───┐                             │           │
│Student│ │Teacher│                             │           │
├───────┤ ├───────┤                             │           │
│studentId│employeeId                           │           │
│year     │specialization                       │           │
│address  │department ──────────────────────────┘           │
│department│courses[]                                       │
│courses[]─────────────────────────────────────────────────┘
└───────┘ └───────┘
```

### Tables

| Table | Description |
|-------|-------------|
| `users` | Base table for user credentials (inherited by students/teachers) |
| `students` | Student-specific data, extends users |
| `teachers` | Teacher-specific data, extends users |
| `departments` | Academic departments |
| `courses` | Course information |
| `student_courses` | Many-to-many junction table for student course enrollment |

---

## 🔒 Security & Authorization

### Role Hierarchy

| Role | Permissions |
|------|-------------|
| **TEACHER** | Full CRUD on Students, Teachers, Courses, Departments |
| **STUDENT** | View courses/students, Edit own profile only |

### Security Features

- **BCrypt Password Encoding**: All passwords are securely hashed
- **Method-Level Security**: `@PreAuthorize` annotations for fine-grained access control
- **Form-Based Authentication**: Custom login page with Spring Security
- **Session Management**: Automatic session handling and logout

---

## 📄 File Descriptions

### Main Application

| File | Description |
|------|-------------|
| `StudentManagementApplication.java` | Spring Boot main entry point. Bootstraps the application context and starts the embedded server. |

### Configuration (`config/`)

| File | Description |
|------|-------------|
| `SecurityConfig.java` | Configures Spring Security: authentication providers, authorization rules, login/logout handling, and password encoding. |
| `DataInitializer.java` | Runs on application startup. Creates default departments (Computer Science, Electrical Engineering) and an admin teacher account if the database is empty. |

### Controllers (`controller/`)

| File | Description |
|------|-------------|
| `HomeController.java` | Handles public pages: home (`/`), login (`/login`), and authenticated dashboard (`/dashboard`). |
| `StudentController.java` | Manages student CRUD operations and student profile self-service. Teachers can create/edit/delete students; students can view/edit their own profile. |
| `TeacherController.java` | Handles teacher CRUD operations. Only accessible to users with TEACHER role. |
| `CourseController.java` | Manages course CRUD operations. Teachers can create/edit/delete; all authenticated users can view. |
| `DeptController.java` | Handles department CRUD operations. Only accessible to users with TEACHER role. |

### DTOs (`dto/`)

| File | Description |
|------|-------------|
| `StudentDTO.java` | Data Transfer Object for student data. Used to transfer data between controller and service layers without exposing JPA entities. |
| `TeacherDTO.java` | Data Transfer Object for teacher data. |
| `CourseDTO.java` | Data Transfer Object for course data. |
| `DeptDTO.java` | Data Transfer Object for department data. |

### Entities (`entity/`)

| File | Description |
|------|-------------|
| `User.java` | Abstract base entity containing common user fields (username, password, email, role). Uses JPA inheritance with JOINED strategy. |
| `Role.java` | Enum defining user roles: `TEACHER` and `STUDENT`. |
| `Student.java` | Entity representing a student. Extends User. Has M:1 relationship with Department and M:M with Courses. |
| `Teacher.java` | Entity representing a teacher. Extends User. Has M:1 relationship with Department and 1:M with Courses. |
| `Course.java` | Entity representing a course. Has M:1 with Department and Teacher, M:M with Students. |
| `Dept.java` | Entity representing an academic department. Has 1:M relationships with Students, Teachers, and Courses. |

### Repositories (`repository/`)

| File | Description |
|------|-------------|
| `UserRepository.java` | JPA repository for User entity. Provides methods to check username/email existence. |
| `StudentRepository.java` | JPA repository for Student entity. Includes custom queries for finding by username, department, and student ID. |
| `TeacherRepository.java` | JPA repository for Teacher entity. Includes custom queries for finding by username. |
| `CourseRepository.java` | JPA repository for Course entity. |
| `DeptRepository.java` | JPA repository for Department entity. Includes custom query for finding by name. |

### Services (`service/`)

| File | Description |
|------|-------------|
| `StudentService.java` | Business logic for student operations. Handles CRUD, profile updates, password encoding, and DTO conversions. |
| `TeacherService.java` | Business logic for teacher operations. Similar structure to StudentService. |
| `CourseService.java` | Business logic for course operations including teacher assignment. |
| `DeptService.java` | Business logic for department operations. |

### Security (`security/`)

| File | Description |
|------|-------------|
| `CustomUserDetailsService.java` | Implements Spring Security's UserDetailsService. Loads user details from the database for authentication. Searches both Student and Teacher repositories. |

### Templates (`resources/templates/`)

| File | Description |
|------|-------------|
| `home.html` | Public landing page |
| `login.html` | Login form page |
| `dashboard.html` | Authenticated user dashboard showing role and navigation options |
| `fragments/layout.html` | Reusable layout fragments (header, navigation, footer) |
| `students/list.html` | Lists all students in a table |
| `students/view.html` | Displays detailed student information |
| `students/form.html` | Create/edit student form |
| `students/profile.html` | Student's own profile view |
| `students/profile-edit.html` | Student self-service profile edit form |
| `teachers/list.html` | Lists all teachers |
| `teachers/view.html` | Displays detailed teacher information |
| `teachers/form.html` | Create/edit teacher form |
| `courses/list.html` | Lists all courses |
| `courses/view.html` | Displays detailed course information |
| `courses/form.html` | Create/edit course form |
| `departments/list.html` | Lists all departments |
| `departments/form.html` | Create/edit department form |

### Configuration Files

| File | Description |
|------|-------------|
| `application.properties` | Application configuration: database connection, JPA settings, security defaults. |
| `pom.xml` | Maven project configuration: dependencies, build plugins, Java version. |
| `compose.yaml` | Docker Compose configuration for PostgreSQL database container. |
| `mvnw` / `mvnw.cmd` | Maven wrapper scripts for Linux/Mac and Windows respectively. |

---

## 🧪 Running Tests

```bash
# Run all tests
.\mvnw.cmd test

# Run with verbose output
.\mvnw.cmd test -X
```

---

## 📝 Development Notes

### Hot Reload
The project includes `spring-boot-devtools` for automatic restart during development. Simply save your changes and the application will reload.

### Database Auto-Update
JPA is configured with `ddl-auto=update`, which automatically updates the database schema based on entity changes. For production, consider using Flyway or Liquibase for migrations.

### Stopping the Application
Press `Ctrl+C` in the terminal to stop the Spring Boot application.

### Stopping Docker
```bash
docker compose down
```

To also remove the database volume:
```bash
docker compose down -v
```

---

## 📜 License

This project is for educational purposes.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
