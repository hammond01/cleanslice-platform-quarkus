param(
    [Parameter(Position = 0)]
    [ValidateSet("init", "dev", "test", "integration", "verify", "db-up", "db-down", "doctor", "scaffold", "release")]
    [string]$Command = "verify",
    [Parameter(Position = 1)]
    [string]$Arg1 = "",
    [Parameter(Position = 2)]
    [string]$Arg2 = ""
)

$ErrorActionPreference = "Stop"

function Get-ContainerRuntime {
    if (Get-Command podman -ErrorAction SilentlyContinue) {
        return "podman"
    }
    if (Get-Command docker -ErrorAction SilentlyContinue) {
        return "docker"
    }
    return $null
}

function Invoke-Compose {
    param([string[]]$Args)
    $runtime = Get-ContainerRuntime
    if (-not $runtime) {
        throw "Neither podman nor docker is available."
    }
    & $runtime compose @Args
}

function Ensure-EnvFile {
    if (!(Test-Path ".env") -and (Test-Path ".env.example")) {
        Copy-Item ".env.example" ".env"
        Write-Host "Created .env from .env.example"
    }
}

function Resolve-EnvValue {
    param(
        [string]$Name,
        [string]$DefaultValue
    )
    if (Test-Path ".env") {
        $line = Get-Content ".env" | Where-Object { $_ -match "^$Name=" } | Select-Object -First 1
        if ($line) {
            return ($line -replace "^$Name=", "")
        }
    }
    return $DefaultValue
}

function To-PascalCase {
    param([string]$InputName)
    $parts = ($InputName -split '[^a-zA-Z0-9]+' | Where-Object { $_ -ne "" })
    if ($parts.Count -eq 0) {
        return ""
    }
    $result = ""
    foreach ($p in $parts) {
        $result += ($p.Substring(0, 1).ToUpper() + $p.Substring(1).ToLower())
    }
    return $result
}

function To-KebabCase {
    param([string]$InputName)
    return (($InputName -creplace '([a-z0-9])([A-Z])', '$1-$2').ToLower())
}

function Get-Plural {
    param([string]$Word)
    if ($Word -match '[^aeiou]y$') {
        return $Word.Substring(0, $Word.Length - 1) + "ies"
    }
    if ($Word -match '(s|x|z|ch|sh)$') {
        return $Word + "es"
    }
    return $Word + "s"
}

function New-SafeFile {
    param(
        [string]$Path,
        [string]$Content
    )
    if (Test-Path $Path) {
        throw "Refusing to overwrite existing file: $Path"
    }
    $dir = Split-Path $Path -Parent
    if (!(Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
    Set-Content -Path $Path -Value $Content -Encoding UTF8
}

function Write-StatusOk {
    param([string]$Message)
    Write-Host "[OK] $Message"
}

function Write-StatusFail {
    param([string]$Message)
    Write-Host "[FAIL] $Message"
}

function Invoke-Doctor {
    param([bool]$StrictMode)

    $failures = 0

    if (Get-Command java -ErrorAction SilentlyContinue) {
        Write-StatusOk "java detected"
    } else {
        Write-StatusFail "java missing"
        $failures++
    }

    if (Test-Path "./gradlew") {
        Write-StatusOk "gradlew detected"
    } else {
        Write-StatusFail "gradlew missing"
        $failures++
    }

    $runtime = Get-ContainerRuntime
    if ($runtime) {
        Write-StatusOk "container runtime detected: $runtime"
    } else {
        Write-StatusFail "container runtime missing (podman/docker)"
        $failures++
    }

    if (Test-Path ".env.example") {
        Write-StatusOk ".env.example detected"
    } else {
        Write-StatusFail ".env.example missing"
        $failures++
    }

    if (Test-Path "app/src/main/resources/application.yml") {
        Write-StatusOk "application.yml detected"
    } else {
        Write-StatusFail "application.yml missing"
        $failures++
    }

    if ($StrictMode) {
        try {
            Invoke-Compose @("ps") | Out-Null
            Write-StatusOk "compose is accessible"
        } catch {
            Write-StatusFail "compose is not accessible or project is not initialized"
            $failures++
        }

        $postgresRunning = $false

        try {
            $runningServices = Invoke-Compose @("ps", "--services", "--status", "running")
            if (($runningServices | Where-Object { $_ -eq "postgres" }).Count -gt 0) {
                Write-StatusOk "postgres service is running"
                $postgresRunning = $true
            }
        } catch {
            Write-StatusFail "unable to check running services"
            $failures++
        }

        if ($runtime) {
            $containerName = Resolve-EnvValue -Name "POSTGRES_CONTAINER_NAME" -DefaultValue "cleanslice-postgres"
            $dbUser = Resolve-EnvValue -Name "DB_USERNAME" -DefaultValue "postgres"
            $dbName = Resolve-EnvValue -Name "POSTGRES_DB" -DefaultValue "cleanslice_platform"
            try {
                & $runtime exec $containerName pg_isready -U $dbUser -d $dbName | Out-Null
                if ($LASTEXITCODE -eq 0) {
                    Write-StatusOk "database readiness check passed"
                    $postgresRunning = $true
                } else {
                    Write-StatusFail "database readiness check failed for container $containerName"
                    $failures++
                }
            } catch {
                Write-StatusFail "database readiness check failed for container $containerName"
                $failures++
            }
        }

        if (-not $postgresRunning) {
            Write-StatusFail "postgres service is not running (run: template.ps1 db-up)"
            $failures++
        }
    }

    if ($failures -gt 0) {
        throw "doctor found $failures issue(s)"
    }

    Write-Host "doctor passed"
}

function Invoke-Scaffold {
    param(
        [string]$FeatureName,
        [bool]$FullMode
    )

    if ([string]::IsNullOrWhiteSpace($FeatureName)) {
        throw "Usage: ./tools/template.ps1 scaffold <feature-name> [--full]"
    }

    $feature = To-PascalCase $FeatureName
    if ([string]::IsNullOrWhiteSpace($feature)) {
        throw "Invalid feature name: $FeatureName"
    }

    $pluralPascal = Get-Plural $feature
    $pluralKebab = Get-Plural (To-KebabCase $feature)

    $domainFile = "app/src/main/java/io/cleanslice/platform/domain/$feature.java"
    $portFile = "app/src/main/java/io/cleanslice/platform/application/port/out/persistence/$($feature)Repository.java"
    $queryFile = "app/src/main/java/io/cleanslice/platform/service/Query$($pluralPascal)UseCase.java"
    $processFile = "app/src/main/java/io/cleanslice/platform/service/Process$($feature)UseCase.java"
    $controllerFile = "app/src/main/java/io/cleanslice/platform/controller/$($feature)Controller.java"
    $adapterFile = "app/src/main/java/io/cleanslice/platform/infrastructure/persistence/repository/$($feature)RepositoryAdapter.java"
    $testFile = "app/src/test/java/io/cleanslice/platform/service/Query$($pluralPascal)UseCaseTest.java"

    New-SafeFile -Path $domainFile -Content @"
package io.cleanslice.platform.domain;

public class $feature extends BaseEntityWithNumber {
    public String name;
    public boolean active = true;
}
"@

    New-SafeFile -Path $portFile -Content @"
package io.cleanslice.platform.application.port.out.persistence;

import io.cleanslice.platform.domain.$feature;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface ${feature}Repository {
    Uni<List<$feature>> findAll();
    Uni<$feature> findById(String number);
    Uni<$feature> save($feature entity);
    Uni<Void> delete($feature entity);
}
"@

    New-SafeFile -Path $queryFile -Content @"
package io.cleanslice.platform.service;

import io.cleanslice.platform.application.port.out.persistence.${feature}Repository;
import io.cleanslice.platform.domain.$feature;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class Query${pluralPascal}UseCase {

    @Inject
    ${feature}Repository repository;

    public Uni<List<$feature>> findAll() {
        return repository.findAll();
    }

    public Uni<$feature> findById(String number) {
        return repository.findById(number);
    }
}
"@

    New-SafeFile -Path $processFile -Content @"
package io.cleanslice.platform.service;

import io.cleanslice.platform.application.port.out.persistence.${feature}Repository;
import io.cleanslice.platform.domain.$feature;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Process${feature}UseCase {

    @Inject
    ${feature}Repository repository;

    @WithTransaction
    public Uni<$feature> create($feature entity) {
        return repository.save(entity);
    }

    @WithTransaction
    public Uni<$feature> update($feature entity) {
        return repository.save(entity);
    }

    @WithTransaction
    public Uni<Void> delete($feature entity) {
        return repository.delete(entity);
    }
}
"@

    New-SafeFile -Path $controllerFile -Content @"
package io.cleanslice.platform.controller;

import io.cleanslice.platform.common.response.ApiResponse;
import io.cleanslice.platform.domain.$feature;
import io.cleanslice.platform.service.Process${feature}UseCase;
import io.cleanslice.platform.service.Query${pluralPascal}UseCase;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/$pluralKebab")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ${feature}Controller {

    @Inject
    Query${pluralPascal}UseCase queryUseCase;

    @Inject
    Process${feature}UseCase processUseCase;

    @GET
    public Uni<ApiResponse<List<$feature>>> findAll() {
        String requestId = UUID.randomUUID().toString();
        return queryUseCase.findAll()
                .onItem().transform(items -> ApiResponse.ok(items, requestId));
    }

    @GET
    @Path("/{number}")
    public Uni<ApiResponse<$feature>> findById(@PathParam("number") String number) {
        String requestId = UUID.randomUUID().toString();
        return queryUseCase.findById(number)
                .onItem().transform(item -> ApiResponse.ok(item, requestId));
    }

    @POST
    public Uni<ApiResponse<$feature>> create($feature request) {
        String requestId = UUID.randomUUID().toString();
        return processUseCase.create(request)
                .onItem().transform(item -> ApiResponse.ok(item, requestId));
    }
}
"@

    New-SafeFile -Path $adapterFile -Content @"
package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.application.port.out.persistence.${feature}Repository;
import io.cleanslice.platform.domain.$feature;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ${feature}RepositoryAdapter implements ${feature}Repository {

    @Override
    public Uni<List<$feature>> findAll() {
        return Uni.createFrom().failure(new UnsupportedOperationException("Implement $feature persistence mapping"));
    }

    @Override
    public Uni<$feature> findById(String number) {
        return Uni.createFrom().failure(new UnsupportedOperationException("Implement $feature persistence mapping"));
    }

    @Override
    public Uni<$feature> save($feature entity) {
        return Uni.createFrom().failure(new UnsupportedOperationException("Implement $feature persistence mapping"));
    }

    @Override
    public Uni<Void> delete($feature entity) {
        return Uni.createFrom().failure(new UnsupportedOperationException("Implement $feature persistence mapping"));
    }
}
"@

    New-SafeFile -Path $testFile -Content @"
package io.cleanslice.platform.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Query${pluralPascal}UseCaseTest {

    @Test
    void scaffoldPlaceholder() {
        assertTrue(true);
    }
}
"@

    if ($FullMode) {
        $createDtoFile = "app/src/main/java/io/cleanslice/platform/dto/Create$($feature)Request.java"
        $responseDtoFile = "app/src/main/java/io/cleanslice/platform/dto/$($feature)Response.java"
        $mapperFile = "app/src/main/java/io/cleanslice/platform/mapper/$($feature)Mapper.java"
        $controllerTestFile = "app/src/test/java/io/cleanslice/platform/controller/$($feature)ControllerTest.java"

        New-SafeFile -Path $createDtoFile -Content @"
package io.cleanslice.platform.dto;

public class Create${feature}Request {
    public String name;
}
"@

        New-SafeFile -Path $responseDtoFile -Content @"
package io.cleanslice.platform.dto;

public class ${feature}Response {
    public String number;
    public String name;
    public boolean active;
}
"@

        New-SafeFile -Path $mapperFile -Content @"
package io.cleanslice.platform.mapper;

import io.cleanslice.platform.domain.$feature;
import io.cleanslice.platform.dto.${feature}Response;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ${feature}Mapper {
    @Mapping(source = "Number", target = "number")
    ${feature}Response toResponse($feature entity);
}
"@

        New-SafeFile -Path $controllerTestFile -Content @"
package io.cleanslice.platform.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ${feature}ControllerTest {

    @Test
    void scaffoldPlaceholder() {
        assertTrue(true);
    }
}
"@
    }

    Write-Host "Scaffold created for feature: $feature"
    Write-Host "Generated endpoint base path: /api/v1/$pluralKebab"
    if ($FullMode) {
        Write-Host "Scaffold mode: full"
    } else {
        Write-Host "Scaffold mode: basic"
    }
}

function Set-ReleaseVersion {
    param(
        [string]$Version,
        [bool]$TagRelease
    )

    if ([string]::IsNullOrWhiteSpace($Version)) {
        throw "Usage: ./tools/template.ps1 release <version> [--tag]"
    }

    if ($Version -notmatch '^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(-[0-9A-Za-z.-]+)?(\+[0-9A-Za-z.-]+)?$') {
        throw "Invalid semantic version: $Version"
    }

    $gradleFile = "app/build.gradle.kts"
    $envFile = ".env.example"

    if (!(Test-Path $gradleFile) -or !(Test-Path $envFile)) {
        throw "Required files for release version bump are missing."
    }

    $gradleContent = Get-Content -Path $gradleFile -Raw
    $gradleContent = [regex]::Replace($gradleContent, '^version\s*=\s*".*"$', "version = `"$Version`"", [System.Text.RegularExpressions.RegexOptions]::Multiline)
    Set-Content -Path $gradleFile -Value $gradleContent -Encoding UTF8

    $envContent = Get-Content -Path $envFile -Raw
    $envContent = [regex]::Replace($envContent, '^APP_VERSION=.*$', "APP_VERSION=$Version", [System.Text.RegularExpressions.RegexOptions]::Multiline)
    Set-Content -Path $envFile -Value $envContent -Encoding UTF8

    Write-Host "Version updated to $Version in:"
    Write-Host "- $gradleFile"
    Write-Host "- $envFile"

    if ($TagRelease) {
        if (!(Get-Command git -ErrorAction SilentlyContinue)) {
            throw "git is required for --tag"
        }
        $tagName = "v$Version"
        $existing = (& git rev-parse $tagName 2>$null)
        if ($LASTEXITCODE -eq 0) {
            throw "Tag already exists: $tagName"
        }
        & git tag -a $tagName -m "release $tagName"
        Write-Host "Created annotated git tag: $tagName"
    }
}

switch ($Command) {
    "init" {
        Ensure-EnvFile
        Invoke-Compose @("up", "-d")
        Write-Host "Template initialized. Next: ./tools/template.ps1 verify"
    }
    "db-up" {
        Invoke-Compose @("up", "-d")
    }
    "db-down" {
        Invoke-Compose @("down")
    }
    "dev" {
        ./gradlew :app:quarkusDev
    }
    "test" {
        ./gradlew :app:test
    }
    "integration" {
        $env:RUN_DB_INTEGRATION_TESTS = "true"
        ./gradlew :app:integrationTest --rerun-tasks
    }
    "verify" {
        ./gradlew :app:test
        $env:RUN_DB_INTEGRATION_TESTS = "true"
        ./gradlew :app:integrationTest
    }
    "doctor" {
        Invoke-Doctor -StrictMode ($Arg1 -eq "--strict")
    }
    "scaffold" {
        Invoke-Scaffold -FeatureName $Arg1 -FullMode ($Arg2 -eq "--full")
    }
    "release" {
        Set-ReleaseVersion -Version $Arg1 -TagRelease ($Arg2 -eq "--tag")
    }
}
