# Reactive Migration Report

## Scope

This report captures the current reactive baseline of the modular monolith template.

## Runtime baseline

- Quarkus 3.29.4
- Java 21
- Hibernate Reactive Panache
- Reactive PostgreSQL driver
- Mutiny `Uni<T>` across controllers, services, and repository ports

## Current reactive model

- Controller methods return `Uni<...>`.
- Service/use-case methods orchestrate with reactive operators.
- Port contracts use `Uni<T>`, `Uni<List<T>>`, `Uni<Void>`.
- Write paths use `@WithTransaction`.
- Read/session paths in adapters use reactive session handling.

## Request flow

```text
HTTP -> Controller -> Service/UseCase -> Port -> Adapter -> Reactive persistence
```

## Observability in reactive flow

- Access telemetry via filter layer
- Structured application/error/performance logs via helper services
- Audit event flow via in-process adapter

## Quality status

- Architecture rules are enforced in test suite.
- Unit and integration test lanes are separated.
- Integration tests are DB-backed and run in CI with PostgreSQL service.

## Remaining improvement points

- Remove deprecated Hibernate ORM config usage in test profile (`database.generation`) over time.
- Expand reactive failure-path test coverage per module.
- Add load/perf benchmark baseline for release gates.

## Guardrails for future changes

- Do not add blocking JDBC calls into reactive request flow.
- Keep side effects chained in the same reactive pipeline.
- Keep new ports reactive.
- Keep transaction and session boundaries explicit and consistent.
