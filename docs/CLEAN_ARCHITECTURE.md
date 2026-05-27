# Clean Architecture Guide

This document defines the architecture contract of the current template.

## Objective

- Keep domain and use-case logic independent from transport and persistence frameworks.
- Make dependencies explicit and testable.
- Keep template scalable for multiple teams in one runtime.

## Package layout

```text
io.cleanslice.platform
├── domain
├── application
│   └── port
│       ├── in
│       │   └── messaging
│       └── out
│           ├── messaging
│           └── persistence
├── service
├── controller
├── infrastructure
│   ├── adapter
│   ├── persistence
│   │   ├── entity
│   │   ├── mapper
│   │   └── repository
│   └── filter
├── dto
├── mapper
└── common
```

## Dependency direction

```text
controller -> service -> domain
                |
                -> application.port <- infrastructure
```

Rules:

- `domain` never depends on `service`, `controller`, `infrastructure`.
- `service` may depend on `domain`, `application.port`, `mapper`, `dto`, `common`.
- `controller` may depend on `service`, `dto`, `common`.
- `infrastructure` implements ports and can depend on framework components.

## Enforced architecture tests

`app/src/test/java/io/cleanslice/platform/architecture/ArchitectureRulesTest.java`

Current enforced constraints:

- service must not depend on controller/infrastructure
- controller must not depend on infrastructure/ports
- domain must not depend on outer layers
- outbound persistence ports must be interfaces
- inbound/outbound messaging ports must be interfaces

## Port and adapter conventions

- Outbound persistence port:
  - `application.port.out.persistence.<Feature>Repository`
- Outbound messaging port:
  - `application.port.out.messaging.<Feature>PublisherPort`
- Inbound messaging port:
  - `application.port.in.messaging.<Feature>ConsumerPort`
- Adapter implementation:
  - `infrastructure.persistence.repository.<Feature>RepositoryAdapter`
  - `infrastructure.adapter.<Feature>Adapter`

## Use case conventions

- Query use cases: `Query*UseCase`
- Command/process use cases: `Process*UseCase`
- Domain-facing orchestration: keep in `service`
- HTTP contract composition: keep in `controller`

## Adding a new feature module

1. Add/extend domain model in `domain`.
2. Define required outbound ports in `application.port.out.*`.
3. Implement use-case logic in `service`.
4. Implement adapters in `infrastructure`.
5. Expose endpoint in `controller` if needed.
6. Add unit tests and architecture-safe integration tests.

## Anti-patterns to avoid

- Injecting infrastructure adapters directly into services.
- Putting framework-specific annotations or behavior in domain models.
- Business branching in controllers.
- Reusing DTO classes as persistence entities.

## Template status

- This repository is a modular monolith template, not a multi-deploy microservice setup.
- Historical service labels used in logs are logical names, not deployment boundaries.
