# VAWT Power Calculator

A Spring Boot application for estimating the output of a vertical-axis wind turbine (VAWT). It provides a server-rendered calculator page, persists successful calculations in PostgreSQL, exposes a REST API, and displays the five most recent saved results.

## What it does

The calculator accepts wind speed, turbine height, rotor diameter, air density, and power coefficient (`Cp`). It calculates:

- Swept area: `height × diameter`
- Raw wind power: `(0.5 × air density × swept area × wind speed³) / 1000`
- Net usable power: `raw wind power × Cp`

Inputs must be non-negative. A `Cp` higher than the Betz limit (`0.593`) is accepted but shown as a warning in the web interface.

Each successful calculation is stored in PostgreSQL. The home page and `GET /api/calculations` show the five most recent entries by default.

## Repository structure

```text
.
├── compose.yaml                         # Local app + PostgreSQL stack
├── Dockerfile                           # Dependency, build, and runtime image stages
├── .dockerignore                        # Files excluded from Docker build context
├── .env.example                         # Safe environment-variable template
├── pom.xml                              # Maven dependencies and build configuration
└── src
    ├── main
    │   ├── java/com/example/wt
    │   │   ├── WindTurbineApplication.java      # Spring Boot entry point and web controller
    │   │   └── calculation
    │   │       ├── CalculationRequest.java      # Validated API request model
    │   │       ├── CalculationResponse.java     # API response model
    │   │       ├── CalculationRestController.java
    │   │       ├── CalculationResult.java       # JPA entity
    │   │       ├── CalculationResultRepository.java
    │   │       └── CalculationService.java      # Calculation and persistence logic
    │   └── resources
    │       ├── application.properties           # Shared JPA and Flyway settings
    │       ├── application-local.properties     # Local PostgreSQL profile
    │       ├── application-neon.properties      # Neon PostgreSQL profile
    │       ├── db/migration/V1__create_calculation_results.sql
    │       ├── static/css/styles.css
    │       └── templates/index.html
    └── test
        └── java/com/example/wt/WtApplicationTests.java
```

## Prerequisites

- Java 17
- Docker Desktop with Docker Compose (recommended for local setup)
- Maven, or the included Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Run with Docker Compose

1. Create a local environment file.

   ```powershell
   Copy-Item .env.example .env
   ```

2. Change `DATABASE_PASSWORD` in `.env` from its example value.

3. Build and start the application and PostgreSQL.

   ```powershell
   docker compose up --build
   ```

4. Open [http://localhost:8080](http://localhost:8080).

Compose starts PostgreSQL on port `5432` and the application on port `8080`. PostgreSQL data is retained in the `postgres-data` Docker volume. To stop the stack, run:

```powershell
docker compose down
```

## Run Spring Boot locally

Start PostgreSQL first, then set the local profile and database variables. The example below assumes PostgreSQL is available on `localhost:5432`.

```powershell
$env:SPRING_PROFILES_ACTIVE = "local"
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/wind_turbine"
$env:DATABASE_USERNAME = "wind_turbine"
$env:DATABASE_PASSWORD = "your-local-password"
.\mvnw.cmd spring-boot:run
```

If the Maven Wrapper is unavailable, use:

```powershell
mvn spring-boot:run
```

Flyway automatically creates and tracks the `calculation_results` table at startup. Hibernate is configured to validate the schema rather than alter it.

## Connect to Neon PostgreSQL

Set the Neon profile and use credentials from your Neon dashboard. Use a JDBC URL with TLS enabled, preferably the pooled endpoint supplied by Neon.

```powershell
$env:SPRING_PROFILES_ACTIVE = "neon"
$env:DATABASE_URL = "jdbc:postgresql://<neon-host>/<database>?sslmode=require"
$env:DATABASE_USERNAME = "<neon-role>"
$env:DATABASE_PASSWORD = "<neon-password>"
$env:DB_POOL_MAX_SIZE = "5"
.\mvnw.cmd spring-boot:run
```

Do not commit `.env` files, JDBC URLs containing credentials, or Neon passwords. The repository ignores `.env` files; `.env.example` is safe to commit.

## REST API

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/calculations` | Validate, calculate, save, and return a result (`201 Created`). |
| `GET` | `/api/calculations?limit=5` | Return newest saved results. Default limit is 5; accepted range is 1–100. |
| `GET` | `/api/calculations/{id}` | Return a single saved result, or `404` when it does not exist. |

Example request:

```bash
curl -X POST http://localhost:8080/api/calculations \
  -H "Content-Type: application/json" \
  -d '{
    "windSpeed": 12.0,
    "height": 2.0,
    "diameter": 2.0,
    "airDensity": 1.16,
    "powerCoefficient": 0.35
  }'
```

## Configuration profiles

| Profile | Purpose | Required variables |
| --- | --- | --- |
| `local` | PostgreSQL running locally or in Docker Compose | `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` |
| `neon` | Hosted Neon PostgreSQL database | `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`; optional `DB_POOL_MAX_SIZE` |

The Docker Compose application service always uses the `local` profile and supplies its own internal database URL (`jdbc:postgresql://db:5432/...`).
