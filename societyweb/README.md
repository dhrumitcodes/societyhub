# SocietyHub

A multi-tenant platform for college societies: sign-up, join requests, events with registration, announcements and role-based dashboards.

- `societyhub-api`: Spring Boot 3, PostgreSQL, Flyway, JWT
- `societyweb`: React + Vite

## Run it locally

### 1. Database
Create a PostgreSQL database named `societyhub_db`. Flyway creates the tables on first start.

### 2. Backend
Create `societyhub-api/src/main/resources/application-local.properties` (it is gitignored):

```
spring.datasource.url=jdbc:postgresql://localhost:5432/societyhub_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
```

Run `SocietyHubApplication` from IntelliJ, or `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`. It listens on http://localhost:8080.

### 3. Frontend
```
cd societyweb
cp .env.example .env
npm install
npm run dev
```
Open http://localhost:5173.

## First admin
Register an account in the app, then promote it in the database:

```sql
UPDATE users SET role = 'COLLEGE_ADMIN' WHERE email = 'you@college.edu';
```

Sign in again. The admin can create societies from the Societies page and then add a president from that society's Members page.

## Roles
- Global: `STUDENT`, `MEMBER`, `SOCIETY_ADMIN`, `COLLEGE_ADMIN`
- Per society: `PRESIDENT`, `VICE_PRESIDENT`, `SECRETARY`, `TREASURER`, `MEMBER`

## Run the tests

```
cd societyhub-api
./mvnw test
```

The tests cover event capacity and registration rules, member and president rules, join requests and the permission checks behind every `@PreAuthorize`.

## Run everything with Docker

```
cp .env.example .env      # then edit .env: set POSTGRES_PASSWORD and JWT_SECRET
docker compose up --build
```

- Web app: http://localhost:3000
- API: http://localhost:8080
- The database lives in a Docker volume called `pgdata`, so it survives restarts. `docker compose down -v` wipes it.

For a real deployment, set `WEB_ORIGIN` and `API_PUBLIC_URL` in `.env` to your actual domains and put both behind HTTPS.

## Before you go live

- Use a long random `JWT_SECRET` from the environment, never a value committed to the repo.
- Never commit `.env` or `application-local.properties`.
- Change the default admin and student passwords you used while developing.