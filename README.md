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

## Profiles

The application uses Spring Profiles to separate development and production settings. The default active profile is **dev**.

### Development (`dev`)

- In-memory **H2** database with `create-drop` schema
- H2 console enabled at `/h2-console`
- Sample categories and products seeded on startup
- Verbose SQL logging and **DEBUG** log level for `com.shop`

**Command line:**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**IDE (IntelliJ / VS Code):** set active profile to `dev` in run configuration, or add VM option:

```
-Dspring.profiles.active=dev
```

### Production (`prod`)

- **PostgreSQL** persistent database (configure via environment variables)
- H2 console disabled, `ddl-auto=validate`
- **WARN** log level for `com.shop` and root logger

**Command line:**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Environment variables (prod):**

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/shopdb` | PostgreSQL JDBC URL |
| `DATABASE_USER` | `shop` | Database username |
| `DATABASE_PASSWORD` | `shop` | Database password |
| `EXTERNAL_SERVICE_URL` | `https://api.shop.com` | External service endpoint |

**IDE:** set active profile to `prod` in run configuration.

## Custom configuration properties

Custom settings are defined under the `app.settings` prefix (`AppSettingsProperties`):

| Property | Role |
|----------|------|
| `app.settings.application-title` | Application display name shown in metadata and admin dashboard |
| `app.settings.pagination-limit` | Default pagination limit exposed via `/api/info` |
| `app.settings.contact-email` | Support contact email returned in application metadata |
| `app.settings.external-service-url` | URL of an external service integrated with the shop |

These properties are validated at startup (`@NotBlank`, `@Min`, `@Email`) and injected into `AppInfoController` and `AdminController`.

## Internationalization (i18n)

The API supports **Georgian** (default) and **English** via the `Accept-Language` HTTP header.

Resource bundles: `messages.properties` (Georgian) and `messages_en.properties` (English), UTF-8 encoded.

### Localized endpoints and messages

| Area | Details |
|------|---------|
| `GET /api/info` | Returns localized `description` field |
| `GET /api/admin/dashboard` | Returns localized welcome `message` |
| `GET /api/products/{id}` (404) | Localized not-found error |
| `GET /api/categories/{id}` (404) | Localized not-found error |
| `POST /api/products`, `POST /api/categories` (400) | Localized validation errors for name fields |
| `401` / `403` responses | Localized authentication and access-denied messages |
| Global exception handler | Localized validation, conflict, and server error messages |

### Testing i18n

**English:**

```bash
curl -H "Accept-Language: en" http://localhost:8080/api/info
```

**Georgian (default):**

```bash
curl -H "Accept-Language: ka" http://localhost:8080/api/info
```

**Localized 404:**

```bash
curl -H "Accept-Language: en" http://localhost:8080/api/products/9999
```

**Localized validation error (admin required for categories):**

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Accept-Language: en" \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{"name":"","description":"test"}'
```

## Structured logging

Logging uses **SLF4J** via Lombok `@Slf4j` in services, controllers, security handlers, and the global exception handler.

| Level | Usage |
|-------|-------|
| `DEBUG` | Read operations, locale resolution (dev profile) |
| `INFO` | User seeding, resource creation/update/delete, admin dashboard access |
| `WARN` | Access denied, duplicate resources, not-found errors |
| `ERROR` | Unexpected exceptions, data integrity violations |

### Log file location

Log entries are written to the console and to a rolling file:

```
logs/app.log
```

Rotated daily as `logs/app.YYYY-MM-DD.log` (30-day retention, 100 MB total cap).

Log levels are profile-driven via `logback-spring.xml`: **DEBUG** for `com.shop` in `dev`, **WARN** in `prod`.

## How to run

```bash
mvn spring-boot:run
```

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **Application info:** http://localhost:8080/api/info  
- **Login page:** http://localhost:8080/login.html  
- **H2 console (dev only):** http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:shopdb`, user: `sa`, no password)

## Authentication

Two mechanisms are supported:

1. **Form login** — open `/login.html`, submit username/password (session cookie).
2. **HTTP Basic** — send `Authorization: Basic <base64(username:password)>` header (works in Swagger “Authorize” and Postman).

**Logout:** `POST /logout` (browser session) or close the session.

## Endpoint access rules

### Public (no login)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/info` | Application metadata |
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
- Spring Data JPA, H2 (dev), PostgreSQL (prod)
- Spring Security (BCrypt, form login, HTTP Basic, method security)
- Springdoc OpenAPI (Swagger UI)
- SLF4J / Logback structured logging
- Spring i18n (`MessageSource`, `AcceptHeaderLocaleResolver`)
