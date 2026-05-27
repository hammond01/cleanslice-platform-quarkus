# Auto Logging Guide

This guide covers what is automatic and what still requires explicit wrapping.

## Fully automatic

- Access logs from `AccessLogFilter`
- Global error logging from exception handling flow

No controller/service code is needed for these.

## Semi-automatic

Performance logging for DB operations requires wrapping in service flow:

```java
return DatabaseOperationLogger.logPersist(entity, repository.save(entity));
```

Available helpers:

- `logPersist(entity, uni)`
- `logUpdate(entity, uni)`
- `logDelete(entityName, uni)`
- `logOperation(operation, entityName, uni)`

## Manual application logs

Use `LoggingHelper` for explicit business checkpoints:

```java
loggingHelper.logApp(LogLevel.INFO, "Product created", userId, correlationId);
```

## Recommended placement

- Access logs: filter layer only
- Application logs: use-case/service boundary
- Performance logs: around repository calls
- Error logs: global flow plus optional local context logs

## Common mistakes

- Duplicating the same log event in controller and service.
- Logging entire request payloads with sensitive data.
- Logging side effects outside the reactive chain.
- Using inconsistent correlation ID strategy.
