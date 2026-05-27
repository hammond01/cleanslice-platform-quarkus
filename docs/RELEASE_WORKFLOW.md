# Release Workflow

Recommended baseline release flow for this template.

## Branching

- `main`: protected release branch
- `feature/*`: feature development
- `hotfix/*`: urgent production fix

## Pull request gates

- Dependency review (GitHub Action)
- Unit + architecture tests (`:app:test`)
- Integration tests (`:app:integrationTest`)

## Versioning

Use Semantic Versioning:

- `MAJOR`: breaking API or architecture changes
- `MINOR`: backward-compatible features
- `PATCH`: backward-compatible fixes

## Release steps

1. Update version in `app/build.gradle.kts`.
2. Update `CHANGELOG.md`.
3. Create release PR to `main`.
4. Ensure CI is fully green.
5. Tag release (`vX.Y.Z`).

CLI shortcut:

```bash
./tools/template.sh release X.Y.Z --tag
```

## Post-release

- Create next snapshot version (`X.Y.(Z+1)-SNAPSHOT`).
- Track migration notes if schema changed.
