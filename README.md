# CleanSlice Platform Template (Quarkus + Clean Architecture)

Production-oriented template for building a reactive modular monolith on Quarkus with clear Clean/Hexagonal boundaries.

## Why this template

- Single runtime, modular boundaries.
- Clean Architecture package rules enforced by tests.
- Reactive first (`Uni<T>`, Hibernate Reactive, reactive PostgreSQL client).
- Flyway migration baseline included.
- CI split for fast unit checks and DB-backed integration checks.
- Structured logging and audit pipeline ready out of the box.

## Tech stack

- Java 21
- Quarkus 3.29.4
- Hibernate Reactive Panache
- Reactive PostgreSQL client
- Flyway
- MapStruct
- JUnit 5 + Mockito + ArchUnit + RestAssured

## Project layout

```text
.
├── app/
│   ├── src/main/java/io/cleanslice/platform
│   │   ├── domain
│   │   ├── application/port
│   │   ├── service
│   │   ├── controller
│   │   ├── infrastructure
│   │   ├── dto
│   │   ├── mapper
│   │   └── common
│   ├── src/main/resources
│   │   ├── application.yml
│   │   └── db/migration
│   └── src/test
├── docker/postgres/init
├── docs
└── .github/workflows
```

## Quick start

Prerequisites:

- JDK 21+
- Docker Compose or Podman Compose

1. Start PostgreSQL:

```bash
docker compose up -d
```

Or:

```bash
podman compose up -d
```

2. Run dev mode:

```bash
./gradlew :app:quarkusDev
```

3. Open API docs:

- Swagger UI: `http://localhost:8080/q/swagger-ui`
- OpenAPI: `http://localhost:8080/q/openapi`

## Build and test

Build:

```bash
./gradlew :app:build
```

Unit + architecture tests:

```bash
./gradlew :app:test
```

Integration tests (opt-in):

```bash
RUN_DB_INTEGRATION_TESTS=true ./gradlew :app:integrationTest
```

PowerShell:

```powershell
$env:RUN_DB_INTEGRATION_TESTS="true"; ./gradlew :app:integrationTest
```

Notes:

- `test` task excludes `@Tag("integration")`.
- `integrationTest` task includes only `@Tag("integration")`.
- Integration test task forces Flyway migrate/clean at start to keep DB state deterministic.

## CLI helpers

PowerShell:

```powershell
./tools/template.ps1 init
./tools/template.ps1 doctor
./tools/template.ps1 doctor --strict
./tools/template.ps1 verify
./tools/template.ps1 scaffold inventory-item --full
./tools/template.ps1 release 1.1.0-SNAPSHOT --tag
```

Bash:

```bash
./tools/template.sh init
./tools/template.sh doctor
./tools/template.sh doctor --strict
./tools/template.sh verify
./tools/template.sh scaffold inventory-item --full
./tools/template.sh release 1.1.0-SNAPSHOT --tag
```

See `docs/TEMPLATE_CLI.md` for all commands.

## Runtime configuration

Main config file: `app/src/main/resources/application.yml`

Profiles:

- `dev`: local defaults (`postgres/postgres`, DB `cleanslice_platform`)
- `test`: DB `cleanslice_platform_test`, Flyway migrate+clean on start
- `prod`: env-driven credentials and URLs, schema validation mode

Template env vars are listed in `.env.example`.

## Database and migrations

- Migration folder: `app/src/main/resources/db/migration`
- Baseline migration: `V1__init_schema.sql`
- Compose init script creates test DB on first container init:
  - `cleanslice_platform`
  - `cleanslice_platform_test`

If your existing DB volume was created with different credentials, reset/recreate the volume before first template run.

## CI pipeline

Workflow: `.github/workflows/ci.yml`

- `dependency-review` job (PR only): checks vulnerable/new risky dependencies
- `test` job: runs `:app:test`
- `integration-test` job: starts PostgreSQL service and runs `:app:integrationTest`

## Architecture guardrails

Enforced by `ArchitectureRulesTest`:

- `service` must not depend on `controller` or `infrastructure`
- `controller` must not depend on `infrastructure` or `application.port`
- `domain` must not depend on outer layers
- port packages must contain interfaces

See `docs/CLEAN_ARCHITECTURE.md` for rules and conventions.

## Logging and observability

- Access logs via HTTP filter
- Error logging via global exception flow
- Performance logging via DB operation wrappers
- Audit event pipeline via in-process port/adapter

See:

- `docs/LOGGING_SYSTEM.md`
- `docs/AUTO_LOGGING_GUIDE.md`
- `docs/AUDIT_LOGGING_GUIDE.md`

## API groups

- Products: `/api/v1/products`
- Categories: `/api/v1/categories`
- Audit: `/api/v1/audit`
- Access logs: `/api/v1/logs/access`
- Application logs: `/api/v1/logs/application`
- Error logs: `/api/v1/logs/error`
- Performance logs: `/api/v1/logs/performance`

## Test examples

- Unit/architecture: `QueryAccessLogsUseCaseTest`, `ArchitectureRulesTest`
- Integration: `ProductApiIntegrationTest`, `CategoryApiIntegrationTest`

## Template customization checklist

See detailed checklist: `docs/TEMPLATE_CUSTOMIZATION.md`

## Documentation index

- `docs/CLEAN_ARCHITECTURE.md`
- `docs/LOGGING_SYSTEM.md`
- `docs/AUTO_LOGGING_GUIDE.md`
- `docs/AUDIT_LOGGING_GUIDE.md`
- `docs/REACTIVE_MIGRATION_REPORT.md`
- `docs/TEMPLATE_CLI.md`
- `docs/TEMPLATE_CUSTOMIZATION.md`
- `docs/RELEASE_WORKFLOW.md`
- `CHANGELOG.md`
