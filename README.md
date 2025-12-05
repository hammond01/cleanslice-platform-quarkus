# CleanSlice Platform - Pharmaceutical POS System

A modern microservices platform built with Quarkus for pharmaceutical and functional food retail, featuring Clean Architecture, reactive programming, and comprehensive audit logging.

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│         Kong API Gateway :8000          │
└────────────┬────────────────────────────┘
             │
    ┌────────┴──────────┬─────────────┬──────────────┐
    │                   │             │              │
┌───▼──────────┐ ┌─────▼────────┐ ┌──▼───────────┐ ┌▼──────────┐
│   Product    │ │   Category   │ │    Audit     │ │   Saga    │
│   Service    │ │   Service    │ │   Service    │ │Orchestrate│
│   :8081      │ │   :8082      │ │   :8083      │ │   :8084   │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘ └───────────┘
       │                │                │
┌──────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐
│ Product DB  │  │Category DB  │  │  Audit DB   │
│   :5432     │  │   :5433     │  │   :5434     │
└─────────────┘  └─────────────┘  └─────────────┘

         ┌─────────────────────────────┐
         │  Kafka Message Bus :9092    │
         │  (Audit Events, Saga)       │
         └─────────────────────────────┘
```

## ✨ Key Features

- **Clean Architecture**: Hexagonal architecture with clear separation of concerns
- **Reactive Programming**: Built on Quarkus Reactive with Mutiny
- **Audit Logging**: Comprehensive audit trail for pharmaceutical compliance
- **Event-Driven**: Kafka-based async messaging for audit events
- **Saga Orchestration**: Distributed transaction management
- **API Gateway**: Kong for routing, rate limiting, and security
- **Multi-Database**: Separate PostgreSQL instances per service
- **POS Context**: Terminal, shift, and pharmacist tracking
- **Compliance-Ready**: Prescription tracking, batch/lot traceability

## 🚀 Quick Start

### Prerequisites
- JDK 21+
- Docker & Docker Compose
- Gradle 8.x
- PowerShell (for Windows) or Bash (for Linux/Mac)

### 1. Clone and Build
```powershell
# Clone repository
git clone https://github.com/hammond01/cleanslice-platform-quarkus.git
cd HONEY_BEE

# Build all services
./gradlew build

# Or build specific services
./gradlew :services:product-service:build
./gradlew :services:category-service:build
./gradlew :services:audit-service:build
```

### 2. Start Infrastructure with Docker Compose
```powershell
# Start all services (databases, Kafka, Kong)
docker-compose up -d

# View logs
docker-compose logs -f

# Check status
docker-compose ps

# Stop all services
docker-compose down
```

### 3. Run Services in Dev Mode
```powershell
# Terminal 1 - Product Service
./gradlew :services:product-service:quarkusDev

# Terminal 2 - Category Service  
./gradlew :services:category-service:quarkusDev

# Terminal 3 - Audit Service
./gradlew :services:audit-service:quarkusDev
```

## 📡 API Endpoints

### Via Kong API Gateway (Port 8010)
- Products: `http://localhost:8010/products`
- Categories: `http://localhost:8010/categories`
- Audit Logs: `http://localhost:8010/audit`

### Direct Service Access (Development)
- Product Service: `http://localhost:8012/api/products`
- Category Service: `http://localhost:8013/api/categories`
- Audit Service: `http://localhost:8011/api/audit`

### Health & Metrics
- Product Health: `http://localhost:8012/q/health`
- Category Health: `http://localhost:8013/q/health`
- Audit Health: `http://localhost:8011/q/health`
- Product Metrics: `http://localhost:8012/q/metrics`

### Documentation & Admin
- Product Swagger: `http://localhost:8012/q/swagger-ui`
- Category Swagger: `http://localhost:8013/q/swagger-ui`
- Audit Swagger: `http://localhost:8011/q/swagger-ui`
- Kong Admin API: `http://localhost:8001`
- Kafka UI: `http://localhost:8090`

## 🧪 Testing APIs

### Create Category
```powershell
curl -X POST http://localhost:8010/categories `
  -H "Content-Type: application/json" `
  -d '{
    "name": "Pharmaceuticals",
    "description": "Prescription and OTC medications",
    "slug": "pharmaceuticals"
  }'
```

### Create Product
```powershell
curl -X POST http://localhost:8010/products `
  -H "Content-Type: application/json" `
  -H "X-Terminal-Id: POS-001" `
  -H "X-Store-Id: STORE-HCM-01" `
  -d '{
    "name": "Paracetamol 500mg",
    "description": "Pain reliever and fever reducer",
    "price": 25000,
    "stock": 1000,
    "categoryNumber": "CAT-001",
    "requiresPrescription": false,
    "batchNumber": "BATCH-2024-001",
    "expiryDate": "2025-12-31"
  }'
```

### Get All Products
```powershell
curl http://localhost:8010/products
```

### Get Product by Number
```powershell
curl http://localhost:8010/products/PROD-001
```

### Update Product
```powershell
curl -X PUT http://localhost:8010/products/PROD-001 `
  -H "Content-Type: application/json" `
  -d '{
    "name": "Paracetamol 500mg",
    "price": 27000,
    "stock": 950
  }'
```

### Delete Product (Soft Delete)
```powershell
curl -X DELETE http://localhost:8010/products/PROD-001
```

## 🗂️ Project Structure

```
HONEY_BEE/
├── services/
│   ├── product-service/           # Product management service
│   │   ├── src/main/java/
│   │   │   ├── application/       # Application layer
│   │   │   │   ├── dto/           # Data transfer objects
│   │   │   │   ├── mapper/        # Entity-DTO mappers
│   │   │   │   ├── port/          # Hexagonal ports
│   │   │   │   └── service/       # Business logic
│   │   │   ├── domain/            # Domain layer
│   │   │   │   ├── entity/        # Domain entities
│   │   │   │   ├── enums/         # Domain enums
│   │   │   │   └── exception/     # Domain exceptions
│   │   │   ├── infrastructure/    # Infrastructure layer
│   │   │   │   ├── messaging/     # Kafka adapters
│   │   │   │   ├── persistence/   # Database adapters
│   │   │   │   └── web/           # REST controllers
│   │   │   └── presentation/      # Presentation layer
│   │   └── build.gradle.kts
│   │
│   ├── category-service/          # Category management service
│   │   └── [Same structure as product-service]
│   │
│   ├── audit-service/             # Audit logging service
│   │   ├── src/main/java/
│   │   │   ├── application/
│   │   │   │   ├── usecase/       # Process audit events
│   │   │   │   └── mapper/
│   │   │   ├── domain/
│   │   │   │   └── entity/        # AuditLog entity
│   │   │   └── infrastructure/
│   │   │       ├── messaging/     # Kafka consumer
│   │   │       └── persistence/   # AuditLog repository
│   │   └── build.gradle.kts
│   │
│   └── saga-orchestration/        # Saga pattern orchestrator
│       └── build.gradle
│
├── share/                          # Shared libraries
│   ├── src/main/java/share/
│   │   ├── context/               # PosContext, UserContext
│   │   ├── enums/                 # Shared enums
│   │   └── util/                  # Utility classes
│   └── build.gradle.kts
│
├── core/                           # Core utilities
│   └── build.gradle.kts
│
├── gateway/                        # Kong API Gateway config
│   ├── kong.yml                   # Kong declarative config
│   ├── docker-compose.yml         # Gateway setup
│   └── configure-kong.ps1         # Setup script
│
├── docs/                           # Documentation
│   ├── AUDIT_LOGGING_GUIDE.md     # Audit system guide
│   ├── CLEAN_ARCHITECTURE.md      # Architecture docs
│   └── REACTIVE_MIGRATION_REPORT.md
│
├── docker-compose.yml              # Main compose file
├── build.gradle.kts                # Root build config
├── settings.gradle.kts             # Module settings
└── README.md                       # This file
```

## 🔧 Configuration

### Service Ports
| Service | Port | Database |
|---------|------|----------|
| Audit Service | 8011 | audit_db (PostgreSQL :5432) |
| Product Service | 8012 | product_db (PostgreSQL :5432) |
| Category Service | 8013 | category_db (PostgreSQL :5432) |
| Saga Orchestrator | 8084 | - |
| Kong Gateway | 8010 | kong (PostgreSQL :5432) |
| Kong Admin | 8001 | - |
| Kafka | 9092 | - |
| Kafka UI | 8090 | - |
| Zookeeper | 2181 | - |
| PostgreSQL | 5432 | All databases |

### Environment Variables
Create `.env` file for local development:
```env
# Database
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# POS Context
STORE_ID=STORE-HCM-01
STORE_NAME=Main Store
```

## 🛠️ Tech Stack

### Backend
- **Framework**: Quarkus 3.6+ (Reactive)
- **Language**: Java 21
- **Reactive**: Mutiny, Vert.x
- **Database**: PostgreSQL 16 (Reactive Client)
- **ORM**: Hibernate Reactive Panache
- **Messaging**: Apache Kafka
- **API Gateway**: Kong
- **Build Tool**: Gradle 8.x

### Architecture Patterns
- **Clean Architecture** (Hexagonal)
- **CQRS** (Command Query Responsibility Segregation)
- **Event Sourcing** (Audit events)
- **Saga Pattern** (Distributed transactions)

### DevOps & Tools
- **Containerization**: Docker & Docker Compose
- **API Documentation**: OpenAPI/Swagger
- **Logging**: Quarkus Logging + Structured logs
- **Health Checks**: MicroProfile Health
- **Metrics**: MicroProfile Metrics

## 📚 Key Features for Pharmaceutical POS

### 1. Audit Logging System
- Complete audit trail for compliance
- Tracks all CRUD operations
- Records prescription validations
- Monitors price changes
- Batch/lot traceability
- See [AUDIT_LOGGING_GUIDE.md](docs/AUDIT_LOGGING_GUIDE.md)

### 2. POS Context Management
```java
@Inject PosContext posContext;

// Set at login/shift start
posContext.setTerminalId("POS-001");
posContext.setStoreId("STORE-HCM-01");
posContext.setShiftId("SHIFT-MORNING");
posContext.setPharmacistId("PHARM-001");
```

### 3. Soft Delete Pattern
- Products and categories use soft delete
- Maintains referential integrity
- Supports audit trail
- `isDeleted` flag with `deletedAt` timestamp

### 4. Event-Driven Architecture
- Kafka-based async messaging
- Audit events published to Kafka
- Consumer pattern in audit-service
- Scalable and decoupled

### 5. Reactive Programming
- Non-blocking I/O
- High performance under load
- Backpressure handling
- Resource efficient

## 🧪 Development

### Running Tests
```powershell
# Run all tests
./gradlew test

# Run specific service tests
./gradlew :services:product-service:test

# Run integration tests
./gradlew integrationTest
```

### Code Quality
```powershell
# Check code style
./gradlew checkstyleMain

# Run static analysis
./gradlew check
```

### Database Migrations
```powershell
# Using Flyway (if configured)
./gradlew flywayMigrate

# Or manual SQL scripts in src/main/resources/db/migration/
```

## 📈 Roadmap

### Current Features ✅
- [x] Clean Architecture implementation
- [x] Reactive programming with Mutiny
- [x] Product & Category management
- [x] Comprehensive audit logging
- [x] Kafka event streaming
- [x] Kong API Gateway
- [x] Multi-database setup
- [x] POS context management
- [x] Soft delete pattern

### In Progress 🚧
- [ ] Saga orchestration implementation
- [ ] User authentication & authorization (JWT)
- [ ] Role-based access control (RBAC)
- [ ] Prescription management module

### Planned 📋
- [ ] Inventory management service
- [ ] Sales transaction service
- [ ] Customer management service
- [ ] Reporting & analytics service
- [ ] Redis caching layer
- [ ] Elasticsearch for audit search
- [ ] Service discovery (Consul)
- [ ] Circuit breaker (Resilience4j)
- [ ] Rate limiting & throttling
- [ ] API versioning
- [ ] GraphQL support
- [ ] Monitoring (Prometheus + Grafana)
- [ ] Distributed tracing (Jaeger)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Kubernetes deployment configs

## 🔒 Security Considerations

### For Production
1. **Authentication**: Implement JWT-based auth
2. **Authorization**: RBAC with pharmacist roles
3. **Encryption**: TLS for all communications
4. **Secrets**: Use secret management (Vault)
5. **Audit**: Enable comprehensive audit logging
6. **Database**: Encrypt data at rest
7. **API Gateway**: Rate limiting, IP whitelisting
8. **Compliance**: HIPAA/GDPR considerations

## 📖 Documentation

- [Audit Logging Guide](docs/AUDIT_LOGGING_GUIDE.md) - Complete audit system documentation
- [Clean Architecture](docs/CLEAN_ARCHITECTURE.md) - Architecture principles and patterns
- [Reactive Migration Report](docs/REACTIVE_MIGRATION_REPORT.md) - Migration to reactive programming

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards
- Follow Clean Architecture principles
- Write reactive code (use Uni/Multi)
- Add comprehensive audit logging
- Include unit and integration tests
- Document public APIs
- Use meaningful variable names
- Keep methods small and focused

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Team

- **Architecture**: Clean Architecture + Hexagonal
- **Development**: Quarkus Reactive Stack
- **Database**: PostgreSQL with Reactive Client
- **Messaging**: Apache Kafka
- **Gateway**: Kong API Gateway

## 🆘 Troubleshooting

### Common Issues

**Port already in use**
```powershell
# Check which process uses port
netstat -ano | findstr :8081

# Kill the process
taskkill /PID <PID> /F
```

**Database connection failed**
```powershell
# Check if PostgreSQL is running
docker ps | findstr postgres

# Restart database
docker-compose restart product-db
```

**Kafka connection failed**
```powershell
# Check Kafka logs
docker-compose logs kafka

# Restart Kafka
docker-compose restart kafka zookeeper
```

**Build failed**
```powershell
# Clean and rebuild
./gradlew clean build

# Skip tests if needed
./gradlew build -x test
```

## 📞 Support

For issues and questions:
- Create an issue in GitHub
- Check documentation in `/docs`
- Review audit logging guide for compliance questions

---

**Built with ❤️ for Pharmaceutical Retail Industry**

## Running the Application in Dev Mode

You can run your application in dev mode that enables live coding using:

```powershell
# Audit Service (Port 8011)
./gradlew :services:audit-service:quarkusDev

# Product Service (Port 8012)
./gradlew :services:product-service:quarkusDev

# Category Service (Port 8013)
./gradlew :services:category-service:quarkusDev
```

> **_NOTE:_** Quarkus now ships with a Dev UI, which is available in dev mode only at:
> - Audit: <http://localhost:8011/q/dev/>
> - Product: <http://localhost:8012/q/dev/>
> - Category: <http://localhost:8013/q/dev/>

## Packaging and Running the Application

The application can be packaged using:

```powershell
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it's not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```powershell
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a Native Executable

You can create a native executable using:

```powershell
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```powershell
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Quarkus Guides

- **REST** ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x
- **REST JSON-B** ([guide](https://quarkus.io/guides/rest#json-serialisation)): JSON-B serialization support for Quarkus REST
- **Reactive PostgreSQL client** ([guide](https://quarkus.io/guides/reactive-sql-clients)): Connect to PostgreSQL using reactive patterns
- **Hibernate Reactive Panache** ([guide](https://quarkus.io/guides/hibernate-reactive-panache)): Reactive ORM with Panache
- **SmallRye Reactive Messaging** ([guide](https://quarkus.io/guides/kafka)): Kafka integration for event streaming
- **OpenAPI** ([guide](https://quarkus.io/guides/openapi-swaggerui)): API documentation with Swagger UI
- **Health Checks** ([guide](https://quarkus.io/guides/microprofile-health)): MicroProfile Health for readiness/liveness probes

## Provided Code

### Reactive REST Resources

All REST endpoints are built using reactive patterns with Quarkus REST and return `Uni<T>` or `Multi<T>` types for non-blocking operations.

Example:
```java
@GET
@Path("/{number}")
public Uni<GetProduct> getProduct(@PathParam("number") String number) {
    return productService.getProductById(number);
}
```

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
