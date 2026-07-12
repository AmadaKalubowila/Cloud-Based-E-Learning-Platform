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
├── config/            # Security, S3, Liquibase, mail, REST client config
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

   [http://localhost:8088/swagger-ui/index.html](http://localhost:8088/swagger-ui/index.html)

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

Swagger UI is the fastest way to explore and test every endpoint without a separate tool like Postman.

1. **Start the app** (see [Getting Started](#getting-started-local-development) above).
2. **Open Swagger UI:**
   - Local: [http://localhost:8088/swagger-ui/index.html](http://localhost:8088/swagger-ui/index.html)
   - Production: `http://<your-ec2-host>:8088/swagger-ui/index.html`
3. **Authenticate first** for any protected endpoint:
   - Expand `POST /user/login` under the **user-controller** section, click **Try it out**, submit valid credentials, and copy the `token` from the response.
   - Click the **Authorize** button (top right, padlock icon), paste the token as `Bearer <your-token>`, and click **Authorize**. Swagger then attaches it automatically to every request you try from the UI.
4. **Try any endpoint:** expand it, click **Try it out**, fill in the parameters/body, and click **Execute**. Swagger shows the live request, response status, response body, and a ready-to-copy cURL command.
5. **Raw OpenAPI JSON** (for importing into Postman/Insomnia instead): [http://localhost:8088/v3/api-docs](http://localhost:8088/v3/api-docs)

> Tip for the project report: Swagger's request/response panel makes clean, consistent screenshots — a good fit for the "API testing (Postman/screenshots)" evidence required in Part 5 of the assignment brief.

## Testing the API with Swagger

The app ships with `springdoc-openapi`, so every controller is auto-documented and testable straight from the browser — no Postman setup required.

1. Start the app locally (see [Getting Started](#getting-started-local-development) above).
2. Open Swagger UI in your browser:

   **Local:** [http://localhost:8088/swagger-ui.html](http://localhost:8088/swagger-ui.html)
   **Production:** `http://<host>:8088/swagger-ui.html`

   Raw OpenAPI JSON (useful for importing into Postman/Insomnia) is available at:

   **Local:** [http://localhost:8088/v3/api-docs](http://localhost:8088/v3/api-docs)

3. **Authenticate first, then authorize Swagger itself:**
   - Expand `POST /user/login` under the *user* group → *Try it out* → enter a valid `username`/`password` → *Execute*.
   - Copy the `token` value from the response body.
   - Click the **Authorize** 🔒 button at the top right of the Swagger UI page.
   - Paste the token (as `Bearer <token>` if the scheme requires the prefix — check the placeholder text in the dialog) → *Authorize* → *Close*.
   - Every subsequent "Try it out" call on protected endpoints (courses, modules, enrollments, assignments, videos) will now automatically include your JWT in the `Authorization` header.

4. **Try an endpoint:** pick any operation (e.g. `GET /courses/getAll`), click *Try it out*, fill in parameters/body, and *Execute* — Swagger shows the live request, response body, status code, and headers.

> If `/swagger-ui.html` 404s, confirm `springdoc-openapi-starter-webmvc-ui` is on the classpath (it's already in `pom.xml`) and that no custom Spring Security rule is blocking `/swagger-ui/**` and `/v3/api-docs/**` — those paths must stay publicly accessible for the docs UI to load.

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
