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
mvn test                         # run tests (no backend tests currently exist)
java -jar target/*.jar           # run locally on port 8081 (set by application.properties)
```

### Frontend (run from `weather-frontend/`)

```bash
npm install
npm start                        # dev server on port 3000
npm run build                    # production build → weather-frontend/build/
npm test                         # interactive test run (watches by default)
CI=true npm run test:ci          # headless with coverage + JUnit XML output (used in CI)
npx jest --testPathPattern=App   # run a single test file
```

### Docker

```bash
docker build -t weather-app:latest .   # multi-stage build; exposes port 8081
```

## Architecture

**Backend** — Spring Boot 3.3.3, Java 21, Maven.

- `WeatherController` — single GET endpoint `/weather/{city}`, CORS open to all origins. Returns `400` with a plain string on any exception.
- `WeatherService` — calls OpenWeatherMap via Java `HttpClient`; caches by `city.toLowerCase()` with Caffeine (max 100 entries, 10-min TTL). Cache miss is logged at INFO level.
- `WeatherResponse` and sibling POJOs (`Main`, `Coord`, `Wind`, `Clouds`, `Sys`, `Weather`) — Jackson-deserialized from the external API response. All use `@JsonIgnoreProperties(ignoreUnknown = true)` and Lombok `@Getter`/`@Setter`. The `Main` class adds computed Fahrenheit conversion methods (`getTempFahrenheit()`, etc.) used directly by the frontend.
- Config in `src/main/resources/application.properties`; the API key comes from `WEATHER_API_KEY` env var (defaults to empty string if unset).
- Swagger UI at `/swagger-ui.html`; raw OpenAPI docs at `/api-docs`. Actuator health at `/actuator/health`.

**Frontend** — React 18, Create React App, axios for HTTP.

- `App.js` hardcodes `http://localhost:8082/weather/{city}` as the backend URL — **this differs from the actual backend port (8081)**; update if running locally without the proxy.
- `MyConfirmationModal.js` and `Translator.js` exist in `src/` but are not currently imported by `App.js`.
- Frontend tests (`App.test.js`) use `@testing-library/react` and test only rendering. Axios is auto-mocked via `__mocks__/axios.js` (returns `{ data: {} }` by default).
- Production: frontend build is copied into `src/main/resources/static/` during the Docker build so Spring Boot serves it — no separate server needed.

**Data flow:**

```
React (port 3000 dev / static in prod)
  → GET /weather/{city}
Spring Boot (port 8081 local + container)
  → OpenWeatherMap API (?units=metric → temperatures in °C)
  ← JSON deserialized into WeatherResponse POJOs
  ← Main.getTempFahrenheit() computed on the fly
  ← cached & returned
React renders temp in both °C and °F, description, timezone, city name
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

## Known Quirks

- The hardcoded backend URL in `App.js` (`localhost:8082`) does not match the actual server port (`8081` per `application.properties`). Local dev requires either changing one or setting up a proxy.
- There are no backend unit/integration tests. `mvn test` will succeed vacuously.
- `WeatherInformation.java` is unused (dead code).
