# CleanSlice Platform - Modular Monolith

This project runs as a single Quarkus modular monolith following **Clean Architecture (Hexagonal Architecture)** principles.

## Architecture

The project has been refactored from a multi-service setup into a highly cohesive, standard "Package by Layer" monolith under the `io.cleanslice.platform` package:

- `domain`: Core business logic and entities (independent of frameworks).
- `port`: Interfaces for outbound communication (Repositories, External APIs).
- `service`: Application layer containing use cases and business orchestration.
- `controller`: Inbound primary adapters handling HTTP REST endpoints.
- `infrastructure`: Outbound secondary adapters (Hibernate with Panache, DB connections).
- `dto` & `mapper`: Data Transfer Objects and mapping logic to isolate domain models.
- `common`: Cross-cutting concerns such as standardized Exceptions and Logging.

The old microservice patterns (Kafka topics, saga orchestration, service-by-service runtime) were removed in favor of direct, in-process module communication.

## Run

Prerequisites:
- JDK 21+
- Docker or Podman (for PostgreSQL)

Start database:

```bash
docker compose up -d
# Or if using podman:
podman compose up -d
```

Run monolith in dev mode:

```bash
./gradlew :app:quarkusDev
```

Build production jar:

```bash
./gradlew :app:build
```

## Endpoints

- Products: `http://localhost:8080/api/v1/products`
- Categories: `http://localhost:8080/api/v1/categories`
- Audit Logs: `http://localhost:8080/api/v1/audit`
- OpenAPI: `http://localhost:8080/q/openapi`
- Swagger UI: `http://localhost:8080/q/swagger-ui`

## Notes

- Runtime config is centralized in `app/src/main/resources/application.yml`.
- Database config defaults to PostgreSQL at `localhost:5432`, database `clean-architechture`, user/password `postgres/123456`.
