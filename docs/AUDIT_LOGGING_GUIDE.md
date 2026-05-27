# Audit Logging Guide

Audit logging is implemented in-process through ports and adapters.

## Components

- `dto.AuditEvent`
- `service.AuditHelper`
- `application.port.out.messaging.AuditEventPublisherPort`
- `application.port.in.messaging.AuditEventConsumerPort`
- `infrastructure.adapter.InProcessAuditEventPublisherAdapter`

## Standard flow

1. Use case builds event using `AuditHelper.createBaseEvent(...)`.
2. Use case enriches business fields (`entityType`, `rowId`, `metadata`).
3. Use case publishes event via `publishCrudEvent(...)` or `publishErrorEvent(...)`.
4. Adapter forwards to consumer port for persistence into `audit_logs`.

## Example

```java
AuditEvent event = auditHelper.createBaseEvent("cleanslice-platform", AuditTypeEnum.CRUD, "CREATE");
event.entityType = "Product";
event.rowId = saved.RowId;
event.metadata = "Created product: " + saved.name;
auditHelper.publishCrudEvent(event);
```

## Auto-populated context fields

- `timestamp`
- `correlationId`
- `username`
- `ipAddress`
- `userId` (when parseable to `Long`)

## Usage rules

- Audit CREATE/UPDATE/DELETE and meaningful business failures.
- Keep audit publishing non-blocking to business result.
- Use stable logical `serviceName` values.
- Do not store sensitive personal data in `metadata`.
