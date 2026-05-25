# CleanSlice Platform - Modular Monolith

This project now runs as a single Quarkus modular monolith.

## Architecture

- One deployable runtime: `app`
- Internal modules (same process):
  - `modules:product` (source at `services/product-service`)
  - `modules:category` (source at `services/category-service`)
  - `modules:audit` (source at `services/audit-service`)
- Shared kernel:
  - `core`
  - `share`

The old microservice patterns (Kafka topics, saga orchestration, service-by-service runtime) were removed.
Audit and logging flows are now in-process calls to the audit module.

## Run

Prerequisites:

- JDK 21+
- Docker (for PostgreSQL only)

Start database:

```powershell
docker-compose up -d
```

Run monolith:

```powershell
./gradlew :app:quarkusDev
```

Build:

```powershell
./gradlew :app:build
```

## Endpoints

- Products: `http://localhost:8080/api/products`
- Categories: `http://localhost:8080/api/categories`
- Audit Logs: `http://localhost:8080/api/audit`
- OpenAPI: `http://localhost:8080/q/openapi`
- Swagger UI: `http://localhost:8080/q/swagger-ui`

## Notes

- Runtime config is centralized in `app/src/main/resources/application.yml`.
- Database config defaults to PostgreSQL at `localhost:5432`, database `product_db`, user/password `postgres/postgres`.
