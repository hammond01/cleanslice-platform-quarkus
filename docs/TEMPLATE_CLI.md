# Template CLI Helpers

The repository includes lightweight CLI wrappers for common template operations.

## Files

- PowerShell: `tools/template.ps1`
- Bash: `tools/template.sh`

## Supported commands

- `init`: create `.env` from `.env.example` (if missing) and start DB
- `db-up`: start PostgreSQL container
- `db-down`: stop PostgreSQL container
- `dev`: run Quarkus dev mode
- `test`: run unit + architecture tests
- `integration`: run integration tests with opt-in flag
- `verify`: run `test` then `integration`
- `doctor`: validate local prerequisites
- `doctor --strict`: also checks compose connectivity, running postgres service, and DB readiness
- `scaffold <feature-name>`: generate clean-architecture skeleton files
- `scaffold <feature-name> --full`: generate additional DTO/mapper/controller-test placeholders
- `release <version>`: bump template version in `app/build.gradle.kts` and `.env.example`
- `release <version> --tag`: also creates annotated git tag `v<version>`

## Examples

PowerShell:

```powershell
./tools/template.ps1 init
./tools/template.ps1 doctor
./tools/template.ps1 doctor --strict
./tools/template.ps1 verify
./tools/template.ps1 scaffold inventory-item
./tools/template.ps1 scaffold inventory-item --full
./tools/template.ps1 release 1.1.0-SNAPSHOT --tag
```

Bash:

```bash
./tools/template.sh init
./tools/template.sh doctor
./tools/template.sh doctor --strict
./tools/template.sh verify
./tools/template.sh scaffold inventory-item
./tools/template.sh scaffold inventory-item --full
./tools/template.sh release 1.1.0-SNAPSHOT --tag
```

## Notes

- Runtime detection prefers `podman`, then `docker`.
- `integration` and `verify` enable `RUN_DB_INTEGRATION_TESTS=true`.
- Use `--rerun-tasks` from script command when you need to bypass Gradle task caching.
- `scaffold` will stop if target files already exist.
- `release --tag` creates a local annotated tag only (does not push).
