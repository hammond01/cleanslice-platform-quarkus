# HONEY_BEE Microservices Platform

A modern microservices architecture built with Quarkus, PostgreSQL, and Traefik.

## 🏗️ Architecture

```
┌─────────────────┐
│  Traefik GW     │ :80, :8080
└────────┬────────┘
         │
    ┌────┴─────┐
    │          │
┌───▼──────┐ ┌─▼──────────┐
│ Product  │ │ Category   │
│ Service  │ │ Service    │
│  :8081   │ │  :8082     │
└────┬─────┘ └─────┬──────┘
     │             │
┌────▼─────┐ ┌────▼──────┐
│Product DB│ │Category DB│
│  :5432   │ │  :5433    │
└──────────┘ └───────────┘
```

## 🚀 Quick Start

### Prerequisites
- JDK 21+
- Docker & Docker Compose
- Gradle 8.x

### 1. Build Services
```bash
# Build all services
./gradlew build

# Or build individual service
./gradlew :services:product-service:build
./gradlew :services:category-service:build
```

### 2. Run with Docker Compose
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### 3. Run Services Locally (Dev Mode)
```bash
# Terminal 1 - Product Service
cd services/product-service
../../gradlew quarkusDev

# Terminal 2 - Category Service
cd services/category-service
../../gradlew quarkusDev
```

## 📡 API Endpoints

### Via Gateway (Port 80)
- Products: http://localhost/api/products
- Categories: http://localhost/api/categories

### Direct Access
- Product Service: http://localhost:8081/api/products
- Category Service: http://localhost:8082/api/categories

### Documentation
- Product Swagger: http://localhost:8081/swagger-ui
- Category Swagger: http://localhost:8082/swagger-ui
- Traefik Dashboard: http://localhost:8080

## 🧪 Testing APIs

### Create Category
```bash
curl -X POST http://localhost/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }'
```

### Create Product
```bash
curl -X POST http://localhost/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "stock": 50,
    "categoryId": 1
  }'
```

### Get All Products
```bash
curl http://localhost/api/products
```

### Get All Categories
```bash
curl http://localhost/api/categories
```

## 🗂️ Project Structure

```
honey-bee/
├── services/
│   ├── product-service/
│   │   ├── src/main/java/com/honeybee/product/
│   │   │   ├── entity/Product.java
│   │   │   ├── dto/
│   │   │   ├── service/ProductService.java
│   │   │   └── resource/ProductResource.java
│   │   └── build.gradle.kts
│   └── category-service/
│       ├── src/main/java/com/honeybee/category/
│       │   ├── entity/Category.java
│       │   ├── dto/
│       │   ├── service/CategoryService.java
│       │   └── resource/CategoryResource.java
│       └── build.gradle.kts
├── gateway/
│   └── README.md
├── saga-orchestration/
├── docker-compose.yml
├── build.gradle.kts
└── settings.gradle.kts
```

## 🔧 Configuration

### Database Ports
- Product DB: 5432
- Category DB: 5433

### Service Ports
- Product Service: 8081
- Category Service: 8082
- Traefik Gateway: 80
- Traefik Dashboard: 8080

## 🛠️ Tech Stack

- **Framework**: Quarkus 3.6.4
- **Language**: Java 21
- **Database**: PostgreSQL 16
- **API Gateway**: Traefik v2.10
- **Build Tool**: Gradle 8.x
- **Containerization**: Docker

## 📝 Next Steps

- [ ] Add authentication/authorization (JWT)
- [ ] Implement service-to-service communication
- [ ] Add Redis for caching
- [ ] Setup Kafka for async messaging
- [ ] Add monitoring (Prometheus + Grafana)
- [ ] Implement saga pattern for distributed transactions
- [ ] Add service discovery (Consul)
- [ ] Setup CI/CD pipeline

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- REST JSON-B ([guide](https://quarkus.io/guides/rest#json-serialisation)): JSON-B serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- Reactive PostgreSQL client ([guide](https://quarkus.io/guides/reactive-sql-clients)): Connect to the PostgreSQL database using the reactive pattern

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
