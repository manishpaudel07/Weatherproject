# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This App Does

Full-stack weather application: users search a city name, the React frontend calls the Spring Boot backend, which
fetches live data from the OpenWeatherMap API (cached with Caffeine for 10 minutes) and returns it. The Docker
multi-stage build produces a single container serving both frontend static files and the backend API.

## Commands

### Backend (run from repo root)

```bash
mvn clean package -DskipTests   # build JAR
mvn test                         # run tests
java -jar target/*.jar           # run locally on port 8082
```

### Frontend (run from `weather-frontend/`)

```bash
npm install
npm start                        # dev server on port 3000
npm run build                    # production build → weather-frontend/build/
npm test                         # interactive test run
CI=true npm run test:ci          # headless with coverage (used in CI)
```

### Docker

```bash
docker build -t weather-app:latest .   # multi-stage build; exposes port 8081
```

## Architecture

**Backend** — Spring Boot 3.3.3, Java 21, Maven.

- `WeatherController` — single GET endpoint `/weather/{city}`, CORS open to all origins.
- `WeatherService` — calls `https://api.openweathermap.org/data/2.5/weather` via Java `HttpClient`; Caffeine cache (max
  100 entries, 10-min TTL).
- Config in `src/main/resources/application.properties`; the API key is read from `WEATHER_API_KEY` env var.
- Swagger UI available at `/swagger-ui.html`.

**Frontend** — React 18, Create React App, axios for HTTP, ag-grid-react for data display.

- In development, `App.js` calls `http://localhost:8081/weather/{city}` directly (hardcoded).
- The production Docker image copies the frontend build into `src/main/resources/static/` so Spring Boot serves it as
  static content — no separate frontend server needed.

**Data flow:**

```
React (port 3000 dev / static in prod)
  → GET /weather/{city}
Spring Boot (port 8082 local, 8081 container)
  → OpenWeatherMap API (external)
  ← JSON deserialized into WeatherResponse POJOs
  ← cached & returned
React renders temperature, description, timezone, city name
```

## Deployment

**Docker multi-stage build** (`Dockerfile`):

1. Node 18 — `npm run build`
2. Maven + JDK 21 — `mvn package`, copies frontend build into `resources/static/`
3. JRE 21 — runs the JAR on port 8081

**Infrastructure** (`terraform/main.tf`): AWS EC2 t3.micro (us-east-1), Amazon Linux 2, security group opens ports 22,
3000, 8081.

**CI/CD** (`Jenkinsfile`): Jenkins pipeline triggered on GitHub push — stages: checkout → backend build/test → frontend
build/test → Docker build → Trivy security scan (blocks on HIGH/CRITICAL) → deploy → health check. Supports rollback via
build parameter.

## Environment Variables

| Variable          | Purpose                                      |
|-------------------|----------------------------------------------|
| `WEATHER_API_KEY` | OpenWeatherMap API key (required at runtime) |