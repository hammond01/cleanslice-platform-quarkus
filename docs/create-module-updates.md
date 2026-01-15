# Cập nhật create-module.ps1

## Tổng quan thay đổi

Script `create-module.ps1` đã được cập nhật để phù hợp với cấu trúc thực tế của project **Honey Bee**.

## Các thay đổi chính

### 1. **Cấu trúc thư mục**
- ✅ Thêm folder `presentation/grpc` cho gRPC services
- ✅ Giữ nguyên cấu trúc Clean Architecture với 4 layers

### 2. **build.gradle.kts**
- ✅ Cập nhật Java version từ 17 lên **21** (phù hợp với project)
- ✅ Thay đổi group name từ `honeybee` thành `com.honeybee`
- ✅ Sử dụng `quarkus-rest-jackson` thay vì `quarkus-resteasy-reactive-jackson`
- ✅ Sử dụng `quarkus-messaging-kafka` thay vì `quarkus-smallrye-reactive-messaging-kafka`
- ✅ Thêm dependencies vào **core** và **share** modules
- ✅ Thêm `tasks.withType<JavaCompile>` và `tasks.withType<Test>` configurations
- ✅ Loại bỏ `quarkus-micrometer-registry-prometheus` (không cần thiết cho tất cả services)

### 3. **Base Entities**
- ❌ **LOẠI BỎ** việc tạo `BaseEntity.java` và `BaseEntityWithNumber.java` trong module mới
- ✅ Các entities sẽ sử dụng base classes từ **core** module:
  - `domain.entity.base.BaseEntity`
  - `domain.entity.base.BaseEntityWithNumber`

### 4. **Enums**
- ❌ **LOẠI BỎ** việc tạo `ModificationStatus.java` trong module mới
- ✅ Sử dụng enum từ **share** module: `share.ModificationStatus`

### 5. **Logging Helpers**
- ✅ Giữ nguyên việc tạo các logging helpers (có thể copy từ existing services):
  - `LoggingHelper.java`
  - `DatabaseOperationLogger.java`
  - `AccessLogFilter.java`
  - `GlobalExceptionLogger.java`

### 6. **application.yml**
- ✅ Giữ nguyên cấu trúc Kafka messaging
- ✅ Port mặc định là `0` (auto-assign)

## Cách sử dụng

```powershell
# Tạo module mới
.\create-module.ps1 -moduleName "inventory-service"
```

## Cấu trúc module được tạo

```
services/
└── <module-name>/
    ├── build.gradle.kts
    └── src/
        ├── main/
        │   ├── java/
        │   │   ├── application/
        │   │   │   ├── dto/
        │   │   │   ├── mapper/
        │   │   │   ├── port/
        │   │   │   │   ├── inbound/
        │   │   │   │   └── outbound/
        │   │   │   ├── service/
        │   │   │   └── usecase/
        │   │   ├── domain/
        │   │   │   ├── entity/
        │   │   │   ├── enums/
        │   │   │   ├── event/
        │   │   │   └── exception/
        │   │   ├── infrastructure/
        │   │   │   ├── logging/
        │   │   │   ├── messaging/
        │   │   │   │   └── adapter/
        │   │   │   ├── persistence/
        │   │   │   └── web/
        │   │   └── presentation/
        │   │       ├── rest/
        │   │       └── grpc/
        │   └── resources/
        │       └── application.yml
        └── test/
            ├── java/
            │   ├── application/
            │   ├── domain/
            │   └── infrastructure/
            └── resources/
```

## Dependencies

Module mới sẽ tự động có dependencies vào:

- **core**: Cung cấp base entities và infrastructure utilities
  - `BaseEntity`
  - `BaseEntityWithNumber`
  - `AuditingEntityListener`
  - `UserContext`
  - `UserContextFilter`

- **share**: Cung cấp shared DTOs và enums
  - `ModificationStatus`
  - `ApiResponse`
  - DTOs: `AccessLog`, `ApplicationLog`, `AuditEvent`, `ErrorLog`, `PerformanceLog`
  - Enums: `AuditStatusEnum`, `AuditTypeEnum`, `LogLevel`, `LogType`, `Severity`

## Ví dụ sử dụng Base Entity

```java
package domain.entity;

import domain.entity.base.BaseEntityWithNumber;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product extends BaseEntityWithNumber {
    
    @Column(nullable = false)
    public String name;
    
    @Column(length = 1000)
    public String description;
    
    @Column(nullable = false)
    public BigDecimal price;
    
    @Column(nullable = false)
    public Integer stock;
    
    public boolean active = true;
}
```

## Lưu ý

1. **BaseEntity** đã bao gồm:
   - `RowId` (auto-generated)
   - `createdAt`, `createdBy`
   - `lastModifiedAt`, `lastModifiedBy`
   - `lockedAt`, `lockedBy`
   - `deletedAt`, `deletedBy`
   - `rowVersion` (optimistic locking)
   - `modificationStatus`

2. **BaseEntityWithNumber** kế thừa BaseEntity và thêm:
   - `Number` (String, primary key)

3. Module mới sẽ tự động được thêm vào `settings.gradle.kts`

4. Sau khi tạo module, cần:
   - Tạo entities trong `domain/entity`
   - Tạo DTOs trong `application/dto`
   - Tạo mappers trong `application/mapper`
   - Tạo services trong `application/service`
   - Tạo controllers trong `presentation/rest`
