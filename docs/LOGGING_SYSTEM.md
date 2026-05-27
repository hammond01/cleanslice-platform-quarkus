# Logging System Guide

The template persists structured logs in-process to PostgreSQL, with no message broker dependency.

## Logging domains

- `access_logs`: HTTP request/response telemetry
- `application_logs`: business/application messages
- `error_logs`: exception and failure events
- `performance_logs`: operation duration metrics
- `audit_logs`: business audit trail

## Core components

- `LoggingHelper`
- `AccessLogFilter`
- `GlobalExceptionLogger`
- `DatabaseOperationLogger`
- `AuditHelper`
- `AuditEventPublisherPort` + `InProcessAuditEventPublisherAdapter`

## Runtime flow

1. Request enters API and is captured by `AccessLogFilter`.
2. Service logic emits application and audit events as needed.
3. DB operations can be wrapped for performance timing.
4. Global exception flow captures unhandled errors.
5. Log records are persisted through repository adapters.

## Service naming

- Default service name comes from `quarkus.application.name`.
- Current default is `cleanslice-platform`.

## Query APIs

- Access logs: `/api/v1/logs/access`
- Application logs: `/api/v1/logs/application`
- Error logs: `/api/v1/logs/error`
- Performance logs: `/api/v1/logs/performance`
- Audit logs: `/api/v1/audit`

## Operational recommendations

- Keep correlation IDs stable across request flow.
- Do not store credentials, tokens, or sensitive payloads in logs.
- Keep log field naming consistent to simplify queries and dashboards.
- Treat audit publishing as side-effect best-effort, not core transaction result.
