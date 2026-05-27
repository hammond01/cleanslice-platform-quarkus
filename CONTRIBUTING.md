# Contributing Guide

Thanks for contributing to this Clean Architecture template.

## Workflow

1. Create a feature branch from `main`.
2. Keep each pull request focused on one coherent change.
3. Open a PR using the default template and request review.
4. Merge only after CI is green and approvals are complete.

## Branch and commit conventions

- Branch naming:
  - `feat/<short-topic>`
  - `fix/<short-topic>`
  - `docs/<short-topic>`
  - `chore/<short-topic>`
- Commit message style (recommended): Conventional Commits
  - `feat(module): ...`
  - `fix(ci): ...`
  - `docs(readme): ...`

## Local verification

Run before opening a PR:

```bash
./gradlew :app:test
RUN_DB_INTEGRATION_TESTS=true ./gradlew :app:integrationTest
```

PowerShell:

```powershell
./gradlew :app:test
$env:RUN_DB_INTEGRATION_TESTS="true"; ./gradlew :app:integrationTest
```

## Architecture expectations

- Keep dependency direction aligned with Clean Architecture:
  - `controller -> service -> domain`
  - `infrastructure -> application.port`
- Do not bypass ports from service/controller to infrastructure.
- New persistence/messaging ports belong in `application/port/*` and must be interfaces.

## Documentation requirements

Update docs when behavior changes:

- `README.md` for setup or API surface changes
- `docs/*` for architecture/runtime/logging changes
- `CHANGELOG.md` under `[Unreleased]`
