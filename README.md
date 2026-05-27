# Clean Architecture Modular Monolith Template for Quarkus 3

> A production-minded Clean Architecture starter kit for Java 21 + Quarkus 3 with modular monolith boundaries, reactive persistence, migration tooling, structured logging/auditing, and CLI helpers for fast onboarding.

![Java 21](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Quarkus 3.29.4](https://img.shields.io/badge/Quarkus-3.29.4-4695EB?style=for-the-badge&logo=quarkus&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20Modular%20Monolith-2EA043?style=for-the-badge)
![Reactive](https://img.shields.io/badge/Reactive-Mutiny%20%2B%20Hibernate%20Reactive-8A2BE2?style=for-the-badge)
![Status](https://img.shields.io/badge/status-template%20starter%20kit-0366D6?style=for-the-badge)
![CI](https://img.shields.io/badge/ci--tests-passing-238636?style=for-the-badge)
![License](https://img.shields.io/badge/license-not%20set-6e7781?style=for-the-badge)

---

## Table Of Contents

- What This Project Is
- Current State
- Architecture Overview
- Project Structure
- API Surface
- Quick Start
- Testing
- CLI Helpers
- CI Pipeline
- Collaboration Standards
- Runtime Configuration
- Migrations And Database
- Extending The Template
- Documentation Index

---

## What This Project Is

This repository is a Clean Architecture modular monolith template for Quarkus.

It is designed for:

- teams bootstrapping a new backend with strong architectural boundaries
- developers who want reactive-first defaults
- projects that prefer modular monolith simplicity before distributed complexity
- learning and portfolio use cases where structure and maintainability matter

### What this project is not

This project is not:

- a fully-featured business product
- a microservices deployment template
- a frontend/UI starter

The goal is to provide a reliable backend foundation that teams can extend safely.

---

## Current State

As of the current baseline, this template includes:

- Java 21 + Quarkus 3.29.4 runtime
- Clean Architecture package boundaries with application ports
- Reactive persistence with Hibernate Reactive + PostgreSQL
- Flyway migration baseline (`V1__init_schema.sql`)
- Structured logging and audit modules (in-process)
- Unit + architecture + integration test lanes
- CLI helper scripts for onboarding and release workflows

---

## Architecture Overview

Dependency flow:

```text
controller -> service -> domain
                |
                -> application.port <- infrastructure
```

Key guardrails are enforced by ArchUnit tests:

- `service` must not depend on `controller` or `infrastructure`
- `controller` must not depend on `infrastructure` or `application.port`
- `domain` must not depend on outer layers
- ports under `application.port` must be interfaces

---

## Project Structure

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
├── tools
└── .github/workflows
```

---

## API Surface

- Products: `/api/v1/products`
- Categories: `/api/v1/categories`
- Audit logs: `/api/v1/audit`
- Access logs: `/api/v1/logs/access`
- Application logs: `/api/v1/logs/application`
- Error logs: `/api/v1/logs/error`
- Performance logs: `/api/v1/logs/performance`
- OpenAPI: `/q/openapi`
- Swagger UI: `/q/swagger-ui`

---

## Quick Start

Prerequisites:

- JDK 21+
- Docker Compose or Podman Compose

1. Start PostgreSQL:

```bash
docker compose up -d
```

or

```bash
podman compose up -d
```

2. Run dev mode:

```bash
./gradlew :app:quarkusDev
```

3. Open docs:

- `http://localhost:8080/q/swagger-ui`
- `http://localhost:8080/q/openapi`

---

## Testing

Unit + architecture tests:

```bash
./gradlew :app:test
```

Integration tests:

```bash
RUN_DB_INTEGRATION_TESTS=true ./gradlew :app:integrationTest
```

PowerShell:

```powershell
$env:RUN_DB_INTEGRATION_TESTS="true"; ./gradlew :app:integrationTest
```

Current examples:

- Unit/architecture: `QueryAccessLogsUseCaseTest`, `ArchitectureRulesTest`
- Integration: `ProductApiIntegrationTest`, `CategoryApiIntegrationTest`

---

## CLI Helpers

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

For full command details: `docs/TEMPLATE_CLI.md`

---

## CI Pipeline

Workflow: `.github/workflows/ci.yml`

- `dependency-review` job on pull requests
- `test` job for `:app:test`
- `integration-test` job for `:app:integrationTest` with PostgreSQL service

---

## Collaboration Standards

- Code owners: `.github/CODEOWNERS`
- Pull request template: `.github/pull_request_template.md`
- Issue templates:
  - `.github/ISSUE_TEMPLATE/bug_report.yml`
  - `.github/ISSUE_TEMPLATE/feature_request.yml`
- Contribution guide: `CONTRIBUTING.md`
- Security policy: `SECURITY.md`

`main` is expected to be protected and merged via PR only.

---

## Runtime Configuration

Main config: `app/src/main/resources/application.yml`

Profiles:

- `dev`: local defaults (`postgres/postgres`, `cleanslice_platform`)
- `test`: isolated test DB (`cleanslice_platform_test`) with Flyway migrate/clean strategy
- `prod`: env-driven credentials/URLs with validate strategy

Environment template: `.env.example`

---

## Migrations And Database

- Migration path: `app/src/main/resources/db/migration`
- Baseline: `V1__init_schema.sql`
- Compose init script creates test DB on first bootstrap:
  - `cleanslice_platform`
  - `cleanslice_platform_test`

If using an old persistent DB volume with different credentials/state, recreate volume before first run.

---

## Extending The Template

Recommended flow for a new module:

1. add domain model in `domain`
2. define ports in `application/port/out/*` and `application/port/in/*` when needed
3. implement use cases in `service`
4. implement adapters in `infrastructure`
5. expose API in `controller`
6. add unit + integration coverage

Detailed checklist: `docs/TEMPLATE_CUSTOMIZATION.md`

---

## Documentation Index

- `docs/CLEAN_ARCHITECTURE.md`
- `docs/LOGGING_SYSTEM.md`
- `docs/AUTO_LOGGING_GUIDE.md`
- `docs/AUDIT_LOGGING_GUIDE.md`
- `docs/REACTIVE_MIGRATION_REPORT.md`
- `docs/TEMPLATE_CLI.md`
- `docs/TEMPLATE_CUSTOMIZATION.md`
- `docs/RELEASE_WORKFLOW.md`
- `CONTRIBUTING.md`
- `SECURITY.md`
- `CHANGELOG.md`
