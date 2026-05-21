# Shop API — Spring Boot Midterm + Security

REST API for managing shop categories and products, secured with **Spring Security**.

## Login credentials

| Username | Password  | Roles        |
|----------|-----------|--------------|
| `admin`  | `admin123` | ADMIN, USER  |
| `user`   | `user123`  | USER         |

Passwords are stored in the database using **BCrypt** hashing (never plain text).

## User roles

| Role  | Description |
|-------|-------------|
| **USER**  | Can browse the catalog, manage products (create/update), and view own profile |
| **ADMIN** | Full catalog management: categories CRUD, product delete, admin dashboard |

## How to run

```bash
mvn spring-boot:run
```

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **Login page:** http://localhost:8080/login.html  
- **H2 console:** http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:shopdb`, user: `sa`, no password)

## Authentication

Two mechanisms are supported:

1. **Form login** — open `/login.html`, submit username/password (session cookie).
2. **HTTP Basic** — send `Authorization: Basic <base64(username:password)>` header (works in Swagger “Authorize” and Postman).

**Logout:** `POST /logout` (browser session) or close the session.

## Endpoint access rules

### Public (no login)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products/**` | Browse products |
| GET | `/api/categories/**` | Browse categories |
| GET | `/login.html` | Login page |
| GET | `/swagger-ui/**`, `/api-docs/**` | API documentation |

### Authenticated (USER or ADMIN)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/profile/me` | Current user profile |
| POST | `/api/products` | Create product |
| PUT | `/api/products/{id}` | Update product |

Method-level security also enforces `@PreAuthorize("isAuthenticated()")` on product create/update in the service layer.

### ADMIN only

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST, PUT, DELETE | `/api/categories/**` | Category management |
| DELETE | `/api/products/{id}` | Delete product |
| GET | `/api/admin/dashboard` | Admin statistics dashboard |

Method-level `@PreAuthorize("hasRole('ADMIN')")` is applied on category service methods, product delete, and the admin dashboard.

## ADMIN-only functionality

- **Category CRUD** — only administrators can create, update, or delete categories.
- **Product delete** — only administrators can remove products from the catalog.
- **Admin dashboard** (`GET /api/admin/dashboard`) — returns counts of categories, products, and users.

## CSRF

**CSRF protection is disabled** in `SecurityConfig`.

This project is primarily a **REST API** used with Swagger UI, Postman, and other non-browser clients that do not send CSRF tokens. Disabling CSRF is standard for such APIs when using HTTP Basic or session login from API tools.

For a production browser-only application with form submissions, re-enable CSRF or use token-based authentication (e.g. JWT).

## Security components

| Component | Purpose |
|-----------|---------|
| `SecurityConfig` | `SecurityFilterChain`, login/logout, URL rules, BCrypt encoder |
| `CustomUserDetailsService` | Loads users from the database |
| `DataInitializer` | Seeds default users with encrypted passwords |
| `@EnableMethodSecurity` | Enables `@PreAuthorize` on service methods |

## Tech stack

- Java 21, Spring Boot 3.4.13
- Spring Data JPA, H2
- Spring Security (BCrypt, form login, HTTP Basic, method security)
- Springdoc OpenAPI (Swagger UI)
