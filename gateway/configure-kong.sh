#!/bin/bash
# Kong API Gateway Configuration Script

echo "Configuring Kong Gateway..."

# Configure Product Service
echo "Setting up Product Service route..."
curl -i -X POST http://localhost:8001/services \
  --data "name=product-service" \
  --data "url=http://host.docker.internal:8012"

curl -i -X POST http://localhost:8001/services/product-service/routes \
  --data "paths[]=/api/products" \
  --data "strip_path=false"

# Configure Category Service
echo "Setting up Category Service route..."
curl -i -X POST http://localhost:8001/services \
  --data "name=category-service" \
  --data "url=http://host.docker.internal:8013"

curl -i -X POST http://localhost:8001/services/category-service/routes \
  --data "paths[]=/api/categories" \
  --data "strip_path=false"

# Configure Audit Service
echo "Setting up Audit Service route..."
curl -i -X POST http://localhost:8001/services \
  --data "name=audit-service" \
  --data "url=http://host.docker.internal:8011"

curl -i -X POST http://localhost:8001/services/audit-service/routes \
  --data "paths[]=/api/audit" \
  --data "strip_path=false"

echo ""
echo "Kong Gateway Configuration Complete!"
echo ""
echo "Access services via Kong Gateway (Port 8010):"
echo "  Products:   http://localhost:8010/api/products"
echo "  Categories: http://localhost:8010/api/categories"
echo "  Audit:      http://localhost:8010/api/audit"
echo ""
echo "Direct access (for testing):"
echo "  Product Service:  http://localhost:8012"
echo "  Category Service: http://localhost:8013"
echo "  Audit Service:    http://localhost:8011"
