# Clean Architecture - Folder Structure

Dự án áp dụng **Clean Architecture** (Hexagonal Architecture) với cấu trúc thư mục rõ ràng theo các layer:

## 📁 Cấu trúc chung cho tất cả services

```
com.honeybee.<service>/
├── application/           # Application Layer
│   ├── dto/              # Data Transfer Objects (Request/Response)
│   └── service/          # Application Services (Use Cases)
│
├── domain/               # Domain Layer (Core Business Logic)
│   ├── entity/          # Domain Entities (JPA entities)
│   ├── model/           # Domain Models (Value Objects, Enums)
│   └── repository/      # Repository Interfaces (sẽ thêm khi cần)
│
├── infrastructure/       # Infrastructure Layer
│   ├── persistence/     # Repository Implementations (sẽ thêm khi cần)
│   └── messaging/       # Kafka Publishers/Consumers
│
└── presentation/         # Presentation Layer
    ├── rest/            # REST Controllers
    └── grpc/            # gRPC Services (sẽ thêm khi cần)
```

## 🏗️ Chi tiết từng layer

### 1️⃣ **Domain Layer** - Trung tâm nghiệp vụ
- **entity/**: JPA entities kế thừa từ `PanacheEntity`
  - `Product.java`, `Category.java`, `AuditLog.java`
- **model/**: Enums và Value Objects
  - `AuditType`, `AuditStatus`, `Severity`
- **Không phụ thuộc** vào layer nào khác

### 2️⃣ **Application Layer** - Use Cases
- **dto/**: Request/Response objects cho API
  - `ProductRequest`, `ProductResponse`
  - `AuditEvent` (DTO cho Kafka)
- **service/**: Business logic, orchestration
  - `ProductService`, `CategoryService`, `AuditQueryService`
- Phụ thuộc: `domain`

### 3️⃣ **Infrastructure Layer** - Technical Capabilities
- **messaging/**: Kafka integration
  - `AuditEventPublisher` (Producer)
  - `AuditEventConsumer` (Consumer)
- **persistence/**: Repository implementations (khi cần)
- Phụ thuộc: `domain`, `application`

### 4️⃣ **Presentation Layer** - User Interface
- **rest/**: JAX-RS REST endpoints
  - `ProductResource`, `CategoryResource`, `AuditResource`
- **grpc/**: gRPC services (dự định tương lai)
- Phụ thuộc: `application`

## 📦 Services hiện tại

### Product Service (`services/product-service`)
```
domain/
  entity/Product.java
  model/AuditType.java, AuditStatus.java, Severity.java
application/
  dto/ProductRequest.java, ProductResponse.java, AuditEvent.java
  service/ProductService.java
infrastructure/
  messaging/AuditEventPublisher.java
presentation/
  rest/ProductResource.java
```

### Category Service (`services/category-service`)
```
domain/
  entity/Category.java
  model/AuditType.java, AuditStatus.java, Severity.java
application/
  dto/CategoryRequest.java, CategoryResponse.java, AuditEvent.java
  service/CategoryService.java
infrastructure/
  messaging/AuditEventPublisher.java
presentation/
  rest/CategoryResource.java
```

### Audit Service (`services/audit-service`)
```
domain/
  entity/AuditLog.java, AuditType.java, AuditStatus.java, Severity.java
application/
  dto/AuditEvent.java
  service/AuditQueryService.java
infrastructure/
  messaging/AuditEventConsumer.java
presentation/
  rest/AuditResource.java
```

## 🎯 Lợi ích của cấu trúc này

✅ **Separation of Concerns**: Mỗi layer có trách nhiệm rõ ràng  
✅ **Testability**: Dễ dàng test từng layer độc lập  
✅ **Maintainability**: Code dễ bảo trì, mở rộng  
✅ **Dependency Rule**: Dependencies chỉ đi từ ngoài vào trong  
✅ **Domain-Centric**: Business logic không phụ thuộc framework  

## 🔄 Dependency Flow

```
Presentation → Application → Domain
     ↓              ↓
Infrastructure ----→
```

- **Domain** không phụ thuộc ai
- **Application** chỉ phụ thuộc Domain
- **Infrastructure** phụ thuộc Domain & Application
- **Presentation** phụ thuộc Application

## 📝 Quy tắc phát triển

1. **Domain entities** là PanacheEntity, chứa business rules
2. **DTOs** trong `application/dto`, không dùng entities ở API
3. **Services** trong `application/service`, orchestrate use cases
4. **Controllers** trong `presentation/rest`, chỉ handle HTTP
5. **Infrastructure** chứa technical implementations (Kafka, DB, etc.)

## 🚀 Tương lai

- [ ] Thêm `domain/repository` interfaces
- [ ] Implement `infrastructure/persistence` repositories
- [ ] Thêm `presentation/grpc` khi cần gRPC
- [ ] Thêm `application/usecase` cho complex workflows
- [ ] Implement Auth Service với cấu trúc tương tự
