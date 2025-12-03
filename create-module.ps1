param(
    [Parameter(Mandatory=$true)]
    [string]$moduleName
)

# Base paths
$root = Get-Location
$servicesDir = Join-Path $root "services"
$moduleDir = Join-Path $servicesDir $moduleName

# 1) Create full folder structure (Clean Architecture + DDD)
$folders = @(
    "src/main/java/application/dto",
    "src/main/java/application/mapper",
    "src/main/java/application/port/inbound",
    "src/main/java/application/port/outbound",
    "src/main/java/application/service",

    "src/main/java/domain/entity",
    "src/main/java/domain/enums",
    "src/main/java/domain/event",

    "src/main/java/infrastructure/messaging/adapter",
    "src/main/java/infrastructure/persistence",
    "src/main/java/infrastructure/web",

    "src/main/java/presentation/rest",
    "src/main/java/presentation/grpc",

    "src/main/resources"
)

foreach ($f in $folders) {
    New-Item -ItemType Directory -Force -Path "$moduleDir/$f" | Out-Null
}

# 2) build.gradle.kts
$gradle = @"
plugins {
    id("java")
    id("io.quarkus")
}

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.29.4"))

    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-hibernate-orm-panache")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-smallrye-openapi")
}

group = "honeybee"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
"@
Set-Content "$moduleDir/build.gradle.kts" $gradle -Encoding UTF8

# 3) BaseEntity.java
$baseEntity = @"
package domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    public String createdBy;

    @Column(name = "last_modified_at")
    public LocalDateTime lastModifiedAt;

    @Column(name = "last_modified_by")
    public String lastModifiedBy;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    public String deletedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
"@
Set-Content "$moduleDir/src/main/java/domain/entity/BaseEntity.java" $baseEntity -Encoding UTF8


# 4) BaseEntityWithNumber.java (Number = PK)
$baseEntityNumber = @"
package domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntityWithNumber extends BaseEntity {

    @Id
    @Column(name = "number", nullable = false, unique = true)
    public String number;

    @Column(name = "locked_at")
    public LocalDateTime lockedAt;

    @Column(name = "locked_by")
    public String lockedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "modification_status", nullable = false)
    public ModificationStatus modificationStatus = ModificationStatus.ACTIVE;

    public boolean isLocked() { return lockedAt != null; }

    public void lock(String user) {
        lockedAt = LocalDateTime.now();
        lockedBy = user;
        modificationStatus = ModificationStatus.LOCKED;
    }

    public void unlock() {
        lockedAt = null;
        lockedBy = null;
        modificationStatus = ModificationStatus.ACTIVE;
    }
}
"@
Set-Content "$moduleDir/src/main/java/domain/entity/BaseEntityWithNumber.java" $baseEntityNumber -Encoding UTF8


# 5) ModificationStatus.java
$enum = @"
package domain.enums;

public enum ModificationStatus {
    ACTIVE,
    LOCKED,
    DELETED,
    DRAFT,
    ARCHIVED
}
"@
Set-Content "$moduleDir/src/main/java/domain/enums/ModificationStatus.java" $enum -Encoding UTF8

# 6) application.yml
$yaml = @"
quarkus:
  http:
    port: 0
"@
Set-Content "$moduleDir/src/main/resources/application.yml" $yaml -Encoding UTF8

# 7) auto-add to settings.gradle.kts
$settingsFile = Join-Path $root "settings.gradle.kts"
$includeLine = "include("":services:$moduleName"")"

if ((Get-Content $settingsFile) -notcontains $includeLine) {
    Add-Content $settingsFile $includeLine
}

Write-Host "`nModule '$moduleName' CREATED SUCCESSFULLY (DDD + Clean Architecture folder structure).`n"
