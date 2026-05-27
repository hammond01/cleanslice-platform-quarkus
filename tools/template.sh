#!/usr/bin/env bash
set -euo pipefail

COMMAND="${1:-verify}"
ARG1="${2:-}"
ARG2="${3:-}"

get_container_runtime() {
  if command -v podman >/dev/null 2>&1; then
    echo "podman"
    return
  fi
  if command -v docker >/dev/null 2>&1; then
    echo "docker"
    return
  fi
  return 1
}

compose_cmd() {
  local runtime
  runtime="$(get_container_runtime)"
  "$runtime" compose "$@"
}

ensure_env_file() {
  if [[ ! -f .env && -f .env.example ]]; then
    cp .env.example .env
    echo "Created .env from .env.example"
  fi
}

status_ok() {
  echo "[OK] $1"
}

status_fail() {
  echo "[FAIL] $1"
}

to_pascal() {
  echo "$1" | sed -E 's/[^a-zA-Z0-9]+/ /g' | awk '{for (i=1; i<=NF; i++) { $i=toupper(substr($i,1,1)) tolower(substr($i,2)) } gsub(" ", ""); print }'
}

pluralize() {
  local word="$1"
  if [[ "$word" =~ [^aeiou]y$ ]]; then
    echo "${word%y}ies"
  elif [[ "$word" =~ (s|x|z|ch|sh)$ ]]; then
    echo "${word}es"
  else
    echo "${word}s"
  fi
}

to_kebab() {
  echo "$1" | sed -E 's/([a-z0-9])([A-Z])/\1-\2/g' | tr 'A-Z' 'a-z'
}

create_file() {
  local path="$1"
  if [[ -e "$path" ]]; then
    echo "Refusing to overwrite existing file: $path" >&2
    exit 1
  fi
  mkdir -p "$(dirname "$path")"
  cat > "$path"
}

resolve_env_value() {
  local key="$1"
  local default_value="$2"
  if [[ -f .env ]]; then
    local value
    value="$(grep -E "^${key}=" .env | head -n1 | cut -d= -f2- || true)"
    if [[ -n "$value" ]]; then
      echo "$value"
      return
    fi
  fi
  echo "$default_value"
}

doctor() {
  local strict_mode="$1"
  local failures=0

  if command -v java >/dev/null 2>&1; then
    status_ok "java detected"
  else
    status_fail "java missing"
    failures=$((failures + 1))
  fi

  if [[ -x ./gradlew || -f ./gradlew ]]; then
    status_ok "gradlew detected"
  else
    status_fail "gradlew missing"
    failures=$((failures + 1))
  fi

  if runtime="$(get_container_runtime)"; then
    status_ok "container runtime detected: $runtime"
  else
    status_fail "container runtime missing (podman/docker)"
    failures=$((failures + 1))
  fi

  if [[ -f .env.example ]]; then
    status_ok ".env.example detected"
  else
    status_fail ".env.example missing"
    failures=$((failures + 1))
  fi

  if [[ -f app/src/main/resources/application.yml ]]; then
    status_ok "application.yml detected"
  else
    status_fail "application.yml missing"
    failures=$((failures + 1))
  fi

  if [[ "$strict_mode" == "strict" ]]; then
    if ! compose_cmd ps >/dev/null 2>&1; then
      status_fail "compose is not accessible or project is not initialized"
      failures=$((failures + 1))
    else
      status_ok "compose is accessible"
    fi

    local postgres_running=0
    local running_service
    running_service="$(compose_cmd ps --services --status running 2>/dev/null | grep -E '^postgres$' || true)"
    if [[ -n "$running_service" ]]; then
      postgres_running=1
      status_ok "postgres service is running"
    fi

    if runtime="$(get_container_runtime)"; then
      local container_name db_user db_name
      container_name="$(resolve_env_value "POSTGRES_CONTAINER_NAME" "cleanslice-postgres")"
      db_user="$(resolve_env_value "DB_USERNAME" "postgres")"
      db_name="$(resolve_env_value "POSTGRES_DB" "cleanslice_platform")"

      if "$runtime" exec "$container_name" pg_isready -U "$db_user" -d "$db_name" >/dev/null 2>&1; then
        status_ok "database readiness check passed"
        postgres_running=1
      else
        status_fail "database readiness check failed for container $container_name"
        failures=$((failures + 1))
      fi
    fi

    if [[ "$postgres_running" -eq 0 ]]; then
      status_fail "postgres service is not running (run: template.sh db-up)"
      failures=$((failures + 1))
    fi
  fi

  if [[ "$failures" -gt 0 ]]; then
    echo "doctor found $failures issue(s)" >&2
    exit 1
  fi

  echo "doctor passed"
}

scaffold() {
  local raw_name="$1"
  local mode="$2"
  if [[ -z "$raw_name" ]]; then
    echo "Usage: ./tools/template.sh scaffold <feature-name> [--full]" >&2
    exit 2
  fi

  local feature pascal plural_pascal plural_kebab
  pascal="$(to_pascal "$raw_name")"
  if [[ -z "$pascal" ]]; then
    echo "Invalid feature name: $raw_name" >&2
    exit 2
  fi

  feature="$pascal"
  plural_pascal="$(pluralize "$feature")"
  plural_kebab="$(pluralize "$(to_kebab "$feature")")"

  local domain_file="app/src/main/java/io/cleanslice/platform/domain/${feature}.java"
  local port_file="app/src/main/java/io/cleanslice/platform/application/port/out/persistence/${feature}Repository.java"
  local query_file="app/src/main/java/io/cleanslice/platform/service/Query${plural_pascal}UseCase.java"
  local process_file="app/src/main/java/io/cleanslice/platform/service/Process${feature}UseCase.java"
  local controller_file="app/src/main/java/io/cleanslice/platform/controller/${feature}Controller.java"
  local adapter_file="app/src/main/java/io/cleanslice/platform/infrastructure/persistence/repository/${feature}RepositoryAdapter.java"
  local test_file="app/src/test/java/io/cleanslice/platform/service/Query${plural_pascal}UseCaseTest.java"

  create_file "$domain_file" <<EOF
package io.cleanslice.platform.domain;

public class ${feature} extends BaseEntityWithNumber {
    public String name;
    public boolean active = true;
}
EOF

  create_file "$port_file" <<EOF
package io.cleanslice.platform.application.port.out.persistence;

import io.cleanslice.platform.domain.${feature};
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface ${feature}Repository {
    Uni<List<${feature}>> findAll();
    Uni<${feature}> findById(String number);
    Uni<${feature}> save(${feature} entity);
    Uni<Void> delete(${feature} entity);
}
EOF

  create_file "$query_file" <<EOF
package io.cleanslice.platform.service;

import io.cleanslice.platform.application.port.out.persistence.${feature}Repository;
import io.cleanslice.platform.domain.${feature};
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class Query${plural_pascal}UseCase {

    @Inject
    ${feature}Repository repository;

    public Uni<List<${feature}>> findAll() {
        return repository.findAll();
    }

    public Uni<${feature}> findById(String number) {
        return repository.findById(number);
    }
}
EOF

  create_file "$process_file" <<EOF
package io.cleanslice.platform.service;

import io.cleanslice.platform.application.port.out.persistence.${feature}Repository;
import io.cleanslice.platform.domain.${feature};
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Process${feature}UseCase {

    @Inject
    ${feature}Repository repository;

    @WithTransaction
    public Uni<${feature}> create(${feature} entity) {
        return repository.save(entity);
    }

    @WithTransaction
    public Uni<${feature}> update(${feature} entity) {
        return repository.save(entity);
    }

    @WithTransaction
    public Uni<Void> delete(${feature} entity) {
        return repository.delete(entity);
    }
}
EOF

  create_file "$controller_file" <<EOF
package io.cleanslice.platform.controller;

import io.cleanslice.platform.common.response.ApiResponse;
import io.cleanslice.platform.domain.${feature};
import io.cleanslice.platform.service.Process${feature}UseCase;
import io.cleanslice.platform.service.Query${plural_pascal}UseCase;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/${plural_kebab}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ${feature}Controller {

    @Inject
    Query${plural_pascal}UseCase queryUseCase;

    @Inject
    Process${feature}UseCase processUseCase;

    @GET
    public Uni<ApiResponse<List<${feature}>>> findAll() {
        String requestId = UUID.randomUUID().toString();
        return queryUseCase.findAll()
                .onItem().transform(items -> ApiResponse.ok(items, requestId));
    }

    @GET
    @Path("/{number}")
    public Uni<ApiResponse<${feature}>> findById(@PathParam("number") String number) {
        String requestId = UUID.randomUUID().toString();
        return queryUseCase.findById(number)
                .onItem().transform(item -> ApiResponse.ok(item, requestId));
    }

    @POST
    public Uni<ApiResponse<${feature}>> create(${feature} request) {
        String requestId = UUID.randomUUID().toString();
        return processUseCase.create(request)
                .onItem().transform(item -> ApiResponse.ok(item, requestId));
    }
}
EOF

  create_file "$adapter_file" <<EOF
package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.application.port.out.persistence.${feature}Repository;
import io.cleanslice.platform.domain.${feature};
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ${feature}RepositoryAdapter implements ${feature}Repository {

    @Override
    public Uni<List<${feature}>> findAll() {
        return Uni.createFrom().failure(new UnsupportedOperationException("Implement ${feature} persistence mapping"));
    }

    @Override
    public Uni<${feature}> findById(String number) {
        return Uni.createFrom().failure(new UnsupportedOperationException("Implement ${feature} persistence mapping"));
    }

    @Override
    public Uni<${feature}> save(${feature} entity) {
        return Uni.createFrom().failure(new UnsupportedOperationException("Implement ${feature} persistence mapping"));
    }

    @Override
    public Uni<Void> delete(${feature} entity) {
        return Uni.createFrom().failure(new UnsupportedOperationException("Implement ${feature} persistence mapping"));
    }
}
EOF

  create_file "$test_file" <<EOF
package io.cleanslice.platform.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Query${plural_pascal}UseCaseTest {

    @Test
    void scaffoldPlaceholder() {
        assertTrue(true);
    }
}
EOF

  if [[ "$mode" == "--full" ]]; then
    local create_dto_file="app/src/main/java/io/cleanslice/platform/dto/Create${feature}Request.java"
    local response_dto_file="app/src/main/java/io/cleanslice/platform/dto/${feature}Response.java"
    local mapper_file="app/src/main/java/io/cleanslice/platform/mapper/${feature}Mapper.java"
    local controller_test_file="app/src/test/java/io/cleanslice/platform/controller/${feature}ControllerTest.java"

    create_file "$create_dto_file" <<EOF
package io.cleanslice.platform.dto;

public class Create${feature}Request {
    public String name;
}
EOF

    create_file "$response_dto_file" <<EOF
package io.cleanslice.platform.dto;

public class ${feature}Response {
    public String number;
    public String name;
    public boolean active;
}
EOF

    create_file "$mapper_file" <<EOF
package io.cleanslice.platform.mapper;

import io.cleanslice.platform.domain.${feature};
import io.cleanslice.platform.dto.${feature}Response;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ${feature}Mapper {
    @Mapping(source = "Number", target = "number")
    ${feature}Response toResponse(${feature} entity);
}
EOF

    create_file "$controller_test_file" <<EOF
package io.cleanslice.platform.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ${feature}ControllerTest {

    @Test
    void scaffoldPlaceholder() {
        assertTrue(true);
    }
}
EOF
  fi

  echo "Scaffold created for feature: $feature"
  echo "Generated endpoint base path: /api/v1/$plural_kebab"
  if [[ "$mode" == "--full" ]]; then
    echo "Scaffold mode: full"
  else
    echo "Scaffold mode: basic"
  fi
}

release_version() {
  local version="$1"
  local tag_flag="$2"
  if [[ -z "$version" ]]; then
    echo "Usage: ./tools/template.sh release <version> [--tag]" >&2
    exit 2
  fi
  if [[ ! "$version" =~ ^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(-[0-9A-Za-z.-]+)?(\+[0-9A-Za-z.-]+)?$ ]]; then
    echo "Invalid semantic version: $version" >&2
    exit 2
  fi

  local gradle_file="app/build.gradle.kts"
  local env_file=".env.example"

  if [[ ! -f "$gradle_file" || ! -f "$env_file" ]]; then
    echo "Required files for release version bump are missing." >&2
    exit 1
  fi

  local tmp
  tmp="$(mktemp)"
  awk -v v="$version" '{ if ($0 ~ /^version = "/) print "version = \"" v "\""; else print $0 }' "$gradle_file" > "$tmp" && mv "$tmp" "$gradle_file"

  tmp="$(mktemp)"
  awk -v v="$version" '{ if ($0 ~ /^APP_VERSION=/) print "APP_VERSION=" v; else print $0 }' "$env_file" > "$tmp" && mv "$tmp" "$env_file"

  echo "Version updated to $version in:"
  echo "- $gradle_file"
  echo "- $env_file"

  if [[ "$tag_flag" == "--tag" ]]; then
    if ! command -v git >/dev/null 2>&1; then
      echo "git is required for --tag" >&2
      exit 1
    fi

    local tag_name="v$version"
    if git rev-parse "$tag_name" >/dev/null 2>&1; then
      echo "Tag already exists: $tag_name" >&2
      exit 1
    fi

    git tag -a "$tag_name" -m "release $tag_name"
    echo "Created annotated git tag: $tag_name"
  fi
}

usage() {
  echo "Usage: ./tools/template.sh [init|dev|test|integration|verify|db-up|db-down|doctor [--strict]|scaffold <feature> [--full]|release <version> [--tag]]" >&2
}

case "$COMMAND" in
  init)
    ensure_env_file
    compose_cmd up -d
    echo "Template initialized. Next: ./tools/template.sh verify"
    ;;
  db-up)
    compose_cmd up -d
    ;;
  db-down)
    compose_cmd down
    ;;
  dev)
    ./gradlew :app:quarkusDev
    ;;
  test)
    ./gradlew :app:test
    ;;
  integration)
    RUN_DB_INTEGRATION_TESTS=true ./gradlew :app:integrationTest --rerun-tasks
    ;;
  verify)
    ./gradlew :app:test
    RUN_DB_INTEGRATION_TESTS=true ./gradlew :app:integrationTest
    ;;
  doctor)
    if [[ "$ARG1" == "--strict" ]]; then
      doctor "strict"
    else
      doctor "basic"
    fi
    ;;
  scaffold)
    scaffold "$ARG1" "$ARG2"
    ;;
  release)
    release_version "$ARG1" "$ARG2"
    ;;
  *)
    usage
    exit 2
    ;;
esac
