# Traefik Gateway Configuration for HONEY_BEE Microservices

This gateway uses Traefik v2.10 for routing requests to microservices.

## Features

- **Dynamic Service Discovery**: Automatically discovers services via Docker labels
- **Load Balancing**: Distributes traffic across service instances
- **Dashboard**: Web UI available at http://localhost:8080

## Routing Rules

### Product Service
- **URL**: http://localhost/api/products
- **Port**: 8081
- **Endpoints**:
  - GET /api/products - List all products
  - GET /api/products/{id} - Get product by ID
  - POST /api/products - Create new product
  - PUT /api/products/{id} - Update product
  - DELETE /api/products/{id} - Delete product

### Category Service
- **URL**: http://localhost/api/categories
- **Port**: 8082
- **Endpoints**:
  - GET /api/categories - List all categories
  - GET /api/categories/{id} - Get category by ID
  - POST /api/categories - Create new category
  - PUT /api/categories/{id} - Update category
  - DELETE /api/categories/{id} - Delete category

## Access Points

- **API Gateway**: http://localhost
- **Traefik Dashboard**: http://localhost:8080
- **Product Service Direct**: http://localhost:8081
- **Category Service Direct**: http://localhost:8082
- **Product Swagger UI**: http://localhost:8081/swagger-ui
- **Category Swagger UI**: http://localhost:8082/swagger-ui
