# Changelog

All notable changes to this template should be documented in this file.

The format is based on Keep a Changelog and follows Semantic Versioning.

## [Unreleased]

## [1.0.0] - 2026-05-27

### Added

- Template CLI helpers (`tools/template.ps1`, `tools/template.sh`) for local bootstrap and daily workflows.
- CLI commands:
  - `init`, `db-up`, `db-down`, `dev`, `test`, `integration`, `verify`
  - `doctor`, `doctor --strict`
  - `scaffold <feature-name>`, `scaffold <feature-name> --full`
  - `release <version>`, `release <version> --tag`
- Test suite expansion:
  - `TemplateSmokeTest`
  - `ArchitectureRulesTest`
  - `ProductApiIntegrationTest`
  - `CategoryApiIntegrationTest`
  - unit test support utilities and sample unit tests for service/controller/adapter layers
- CI workflow split and hardening:
  - dedicated unit/architecture lane (`:app:test`)
  - dedicated integration lane (`:app:integrationTest`) with PostgreSQL service
  - pull-request dependency review gate
- Flyway baseline migration scaffold (`V1__init_schema.sql`) and migration-based DB bootstrap.
- Docker/PostgreSQL initialization script to create test DB on first bootstrap.
- New template documentation:
  - `docs/TEMPLATE_CLI.md`
  - `docs/TEMPLATE_CUSTOMIZATION.md`
  - `docs/RELEASE_WORKFLOW.md`
  - `CHANGELOG.md`
- Collaboration and governance templates:
  - `.github/CODEOWNERS`
  - `.github/pull_request_template.md`
  - `.github/ISSUE_TEMPLATE/bug_report.yml`
  - `.github/ISSUE_TEMPLATE/feature_request.yml`
  - `.github/ISSUE_TEMPLATE/config.yml`
- Contributor and security guides:
  - `CONTRIBUTING.md`
  - `SECURITY.md`

### Changed

- Clean Architecture package boundaries refactored:
  - moved legacy `port/*` interfaces to `application/port/in|out/*`
  - updated service/infrastructure dependencies to new port packages
- Repository adapter wiring aligned with new outbound persistence port locations.
- Runtime configuration updated from deprecated Hibernate ORM key to:
  - `quarkus.hibernate-orm.schema-management.strategy`
- Test profile behavior standardized for deterministic integration runs:
  - Flyway migrate/clean defaults for `%test`
  - integration task-level Flyway startup enforcement
- README rewritten for production template onboarding with CLI, CI, DB, and testing workflows.
- Core docs (`CLEAN_ARCHITECTURE`, `LOGGING_SYSTEM`, `AUTO_LOGGING_GUIDE`, `AUDIT_LOGGING_GUIDE`, `REACTIVE_MIGRATION_REPORT`) synchronized to current architecture/runtime.

### Fixed

- `AuditingEntityListener` parameter type mismatch causing runtime failure on persistence callbacks in integration flow.
- Integration boot instability on reused local DB states by hardening test DB/Flyway defaults and scripts.
- `doctor --strict` runtime detection behavior to accept real DB readiness even when compose provider output differs across environments.

### Removed

- Legacy port package interfaces under `app/src/main/java/io/cleanslice/platform/port/*` in favor of `application/port/*`.
- Outdated documentation references tied to prior package structure and template state.
