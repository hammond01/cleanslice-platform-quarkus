# Template Customization Checklist

Use this checklist right after cloning the template for a new project.

## 1) Identity and namespace

- Rename base package from `io.cleanslice.platform` to your project namespace.
- Update `APP_NAME` in `.env.example`.
- Update OpenAPI title/version in `application.yml`.
- Update `group` and `version` in `app/build.gradle.kts`.

## 2) Data model and migrations

- Review `app/src/main/resources/db/migration/V1__init_schema.sql`.
- Remove unused tables from baseline.
- Add your own `V2__...sql`, `V3__...sql` migrations.
- Keep test DB name aligned with project naming.

## 3) Modules and API surface

- Remove unused controllers/services/repositories.
- Keep only domains required by your bounded contexts.
- Add new ports in `application.port` before implementing adapters.

## 4) Observability defaults

- Review log retention strategy and table indexing.
- Confirm audit event fields and sensitive data policy.
- Standardize correlation ID propagation contract.

## 5) CI and release policy

- Configure protected branches and PR rules.
- Keep `test` and `integration-test` lanes enabled.
- Add deployment workflow for your target environment.
- Decide semantic versioning and changelog workflow.

## 6) Security and compliance

- Use non-default DB credentials per environment.
- Add secret scanning and environment protection rules.
- Review dependency updates regularly.

## 7) Developer experience

- Use CLI helpers in `tools/template.ps1` or `tools/template.sh`.
- Create project-specific scripts only after baseline is stable.
- Keep README examples synchronized with actual commands.
