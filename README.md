# Cloud-Based E-Learning Platform — Backend

A Spring Boot REST API backend for a cloud-native e-learning platform, built as part of the Cloud Computing group project (Option A: Cloud-Based E-Learning Platform). It handles user management, course/module management, enrollments, lecturer assignments, and video content delivery via AWS S3.

## Tech Stack

| Layer | Technology |
|---|---|
| Language / Runtime | Java 17 |
| Framework | Spring Boot 4.1 (Web, Security, Data JPA, Validation, Mail, Actuator) |
| Database | MySQL (via Spring Data JPA / Hibernate) |
| Schema Migrations | Liquibase |
| Auth | JWT (jjwt) |
| Object Storage | AWS S3 (video content) |
| API Docs | springdoc-openapi (Swagger UI) |
| Build Tool | Maven (`mvnw` wrapper included) |
| Deployment | AWS EC2 + systemd service |

## Features

- **User Management** — registration, login/logout, JWT refresh, password reset/change, account unlock, role-based users (`STUDENT`, `LECTURER`, `ADMIN`)
- **Course Management** — create, update, fetch, delete, and list (paginated) courses
- **Module Management** — course modules with create/update/fetch/list
- **Enrollments** — enroll/remove students in courses
- **Course Assignments** — assign/remove lecturers to courses
- **Video Content** — upload, create, update, delete, and stream videos via S3, with per-course and per-user listing
- **Audit Logging** — user activity audit trail
- **Health/Metrics** — Spring Actuator endpoints

## Project Structure

```
src/main/java/com/edu/elearning/
├── config/            # Security, S3, Liquibase, mail, REST client, OpenAPI/Swagger bearer-auth config
├── controller/         # REST controllers (course, module, user, video, enrollment, assignment)
├── dto/                # Request/response DTOs, grouped by feature
├── entity/             # JPA entities
├── enums/               # Role, CourseStatus, EnrollmentStatus, Status, Departments
├── exception/          # Custom exceptions + global exception handler
├── repository/         # Spring Data JPA repositories
├── service/            # Service interfaces
├── service/impl/       # Service implementations
├── specification/      # JPA Specification helpers for dynamic queries
└── utility/            # JWT filter/util, sorting helpers

src/main/resources/
├── application.properties          # Shared config (no secrets)
├── application-local.properties    # Local profile (MySQL on localhost, Liquibase on)
├── application-prod.properties     # Prod profile (RDS, no default secrets — fails fast if missing)
└── db/changelog/                   # Liquibase changelogs (master + per-table SQL changesets)

deploy/
├── README.md            # Detailed local vs. production run instructions
├── elearning.service     # systemd unit file for EC2
└── elearning.env.example # Template for the EC2-only env file
```

## Prerequisites

- Java 17 (JDK)
- Maven (or just use the included `./mvnw`)
- MySQL 8.x running locally (for local development)
- An AWS account with an S3 bucket (optional locally — video features are skipped if S3 isn't configured)
- A Gmail account + app password, or another SMTP provider (optional locally — needed for password-reset emails)

## Getting Started (Local Development)

1. **Clone and configure environment variables**

   ```bash
   cp .env.example .env
   ```

   Fill in `.env` with your **own** local values — do not reuse any values that may already exist in this repo's history. At minimum set:

    - `DB_USERNAME` / `DB_PASSWORD` — your local MySQL credentials
    - `JWT_SECRET` — any long random string (a dev default is provided if you skip this)
    - `AWS_*` / `MAIL_*` — optional; leave blank to skip S3/email features locally

2. **Start MySQL** locally and make sure it's reachable at `DB_HOST:DB_PORT` (the app auto-creates the database on first run via `createDatabaseIfNotExist=true`).

3. **Load the env file and run the app**

   ```bash
   set -a && source .env && set +a
   ./mvnw spring-boot:run
   ```

   Or in IntelliJ: *Run > Edit Configurations > Environment variables*, paste the contents of `.env`, and leave `SPRING_PROFILES_ACTIVE` unset (it defaults to `local`).

4. **Verify it's running**

   ```bash
   curl http://localhost:8088/actuator/health
   ```

5. **API docs (Swagger UI)** — once running, visit:

   [http://localhost:8088/swagger-ui/index.html](http://localhost:8088/swagger-ui/index.html) or [http://3.95.246.200/swagger-ui/index.html](http://3.95.246.200/swagger-ui/index.html)

   (`/swagger-ui.html` redirects there too.) The raw OpenAPI spec is at [http://localhost:8088/v3/api-docs](http://localhost:8088/v3/api-docs). Both paths are already allow-listed in `SecurityConfig`, so no token is needed just to view the docs.

On first startup, Liquibase runs the changelogs in `src/main/resources/db/changelog/changes/` to create all tables (`user_details`, `users`, `audit_user_log`, `courses`, `modules`, `course_enrollments`, `course_assignments`, `videos`).

## Environment Variables

| Variable | Required | Description |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | No | `local` (default) or `prod` |
| `SERVER_PORT` | No | Defaults to `8088` |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` | Yes | MySQL connection details |
| `JWT_SECRET` | Yes (prod) | Signing secret for JWTs |
| `AWS_REGION`, `AWS_S3_BUCKET` | For video features | S3 bucket used for video storage |
| `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` | Optional | Leave blank and attach an IAM role instead where possible — `S3Config` falls back to the default AWS credentials provider chain |
| `MAIL_USERNAME`, `MAIL_PASSWORD`, `ADMIN_EMAIL` | For email features | SMTP credentials for password reset emails |

> **Never commit a filled-in `.env` or `elearning.env` file.** Both are already gitignored — keep it that way, and rotate any credential that has ever been committed, even if since removed from `HEAD`.

## Running Tests

```bash
./mvnw test
```

## API Overview

All endpoints (except registration, login, and password reset) require an `Authorization` header with a valid JWT.

| Resource | Base Path | Key Endpoints |
|---|---|---|
| Users | `/user` | `POST /create`, `POST /login`, `POST /logout`, `POST /refresh`, `PUT /password`, `POST /resetPassword`, `PUT /update`, `DELETE /delete`, `GET /getById`, `GET /getAll` |
| Courses | `/courses` | `POST /create`, `PUT /update`, `GET /getById/{id}`, `GET /getAll`, `DELETE /delete/{id}` |
| Modules | `/modules` | `POST /create`, `PUT /update`, `GET /getById/{id}`, `GET /getAll` |
| Enrollments | `/courseEnrollments` | `POST /enrollStudent`, `GET /getEnrollmentById/{id}`, `GET /getAll`, `POST /remove/{id}` |
| Course Assignments | `/courseAssignments` | `POST /assign`, `GET /getAssignmentById/{id}`, `GET /getAll`, `POST /remove/{id}` |
| Videos | `/videos` | `POST /create`, `POST /upload` (multipart), `PUT /update`, `DELETE /delete/{id}`, `GET /getById/{id}`, `GET /getAllByUsers/{id}`, `GET /getAll` |

Full request/response schemas are available via Swagger UI (see [Testing the API with Swagger](#testing-the-api-with-swagger) below) once the app is running. List endpoints support pagination.

**Roles:** `STUDENT`, `LECTURER`, `ADMIN` — access to specific endpoints is scoped by role via Spring Security.

## Testing the API with Swagger

Swagger UI is the fastest way to explore and test every endpoint without a separate tool. As of the `OpenApiConfig` bearer-auth setup, the whole API is wired to a single **Authorize** button — you don't need to enter a token separately on every endpoint.

**1. Start the app** (see [Getting Started](#getting-started-local-development) above).

**2. Open Swagger UI:**
- Local: [http://localhost:8088/swagger-ui/index.html](http://localhost:8088/swagger-ui/index.html)
- Production: [http://3.95.246.200/swagger-ui/index.html](http://3.95.246.200/swagger-ui/index.html)

**3. Get a token:**
- Expand `POST /user/login` (under **user-controller**) → **Try it out** → enter your `userName`/`password` → **Execute**.
- Copy the `accessToken` value from the response body (not `refreshToken`).

**4. Authorize Swagger (do this once per session):**
- Click the green **Authorize** 🔒 button near the top of the page.
- In the dialog, paste your token in the form `Bearer <accessToken>` — the word `Bearer`, one space, then the token. No quotes, no extra words.
- Click **Authorize**, then **Close**.
- Swagger now attaches this header automatically to every request you try — you never touch it again unless the token expires (it's short-lived by design, ~20 minutes; if requests suddenly start failing with 401/403 after a while, just log in again and re-Authorize).

**5. Try any endpoint:** expand it, click **Try it out**, fill in real values (not the placeholder `"string"` text), click **Execute**. Swagger shows the live request, response status, response body, and a matching cURL command.

**6. Raw OpenAPI JSON** (for importing into Postman/Insomnia): [http://localhost:8088/v3/api-docs](http://localhost:8088/v3/api-docs) locally, or [http://3.95.246.200/v3/api-docs](http://3.95.246.200/v3/api-docs) in production.

> Tip for the project report: Swagger's request/response panel makes clean, consistent screenshots — a good fit for the "API testing (Postman/screenshots)" evidence required in Part 5 of the assignment brief.

### How Authorization actually works here (short version)

- Every protected endpoint expects an HTTP header: `Authorization: Bearer <token>`.
- You get the token from `POST /user/login` — it proves who you are and what role you have (`STUDENT`, `LECTURER`, `ADMIN`).
- `JwtFilter` reads that header on every request, checks the token is valid and not expired, and tells Spring Security which role you have. Spring Security then allows or blocks the request based on the rules in `SecurityConfig` (e.g. only `ADMIN` can hit `/modules/create`).
- No header → treated as an anonymous/unauthenticated request → `403` on anything that isn't public.
- Expired or malformed token → same result, `403`.
- The token is *not* saved anywhere server-side — it's just a signed piece of data your client re-sends on every request. Losing it means logging in again; there's nothing to "log out" server-side beyond discarding it (see `POST /user/logout`/`POST /user/refresh` for token lifecycle handling).

## Testing the API with Postman

Postman works identically to Swagger, just outside the browser — useful for saving a reusable collection, chaining requests, or grabbing evidence screenshots for the report.

**1. Log in and grab a token:**
- New request → `POST http://3.95.246.200/user/login` (or `http://localhost:8088/user/login` locally)
- Body tab → **raw** → **JSON**:
  ```json
  {
    "userName": "admin01",
    "password": "Admin@123"
  }
  ```
- Send. Copy `accessToken` from the response.

**2. Set up the Authorization header once, reusably (recommended — avoids retyping it on every request):**
- Create a Postman **Environment** (top-right dropdown → *Add*), name it e.g. `elearning-local` or `elearning-prod`.
- Add a variable `token` and paste the `accessToken` value into *Current Value*.
- On each request, go to the **Authorization** tab → Type: **Bearer Token** → in the Token field enter `{{token}}`.
- Now every request in that environment reuses the same variable — update `token` in one place after each login instead of editing every request.

**3. Call a protected endpoint, e.g. create a module:**
- `POST http://3.95.246.200/modules/create`
- Authorization tab → Bearer Token → `{{token}}` (as set up above)
- Body → raw → JSON:
  ```json
  {
    "moduleCode": "COD1",
    "moduleName": "CS",
    "moduleDescription": "CS intro",
    "moduleCredit": "02"
  }
  ```
- Send. Expect `200 OK` with the created module back, if your logged-in user has the `ADMIN` role required for this endpoint.

**4. Common Postman gotchas:**
- `403 Forbidden` with an empty body → token missing, expired, or the logged-in user's role doesn't match what the endpoint requires (check `SecurityConfig` for the role rules per path).
- `400 Bad Request` → the request reached the server fine, but the JSON body is malformed or missing required fields — check the body tab is set to **raw / JSON**, not **form-data** or **x-www-form-urlencoded**.
- Import `http://3.95.246.200/v3/api-docs` directly into Postman (*Import* → *Link*) to auto-generate a full request collection instead of building each request by hand.



## Deployment (AWS EC2)

Full instructions are in [`deploy/README.md`](deploy/README.md). Summary:

```bash
./mvnw clean package -DskipTests
scp target/elearning-0.0.1-SNAPSHOT.jar ec2-user@<host>:/tmp/elearning.jar
ssh ec2-user@<host> "sudo mv /tmp/elearning.jar /opt/elearning/elearning.jar && sudo systemctl restart elearning"
```

The app runs as a systemd service (`deploy/elearning.service`) reading secrets from `/opt/elearning/elearning.env` (chmod 600, never in git). Production config (`application-prod.properties`) has **no default secrets** — the app deliberately fails to start if a required value is missing, rather than falling back to an insecure default.

## Security Notes

- Passwords are never logged; JWT secret and DB/AWS/mail credentials are read from environment variables only.
- Prefer an EC2 **instance IAM role** over static `AWS_ACCESS_KEY_ID`/`AWS_SECRET_ACCESS_KEY` — `S3Config` already falls back to the default credentials provider chain when these are blank.
- If any secret has ever been committed to this repository (check `deploy/README.md` for a rotation checklist), rotate it and scrub it from git history (e.g. `git filter-repo` or BFG Repo-Cleaner) before deploying or making the repo public.

## License

Academic project — add a license here if you intend to distribute this beyond the coursework submission.
