# Kong API Gateway Configuration

# Start Kong
docker-compose up -d

# Configure Product Service Route
curl -i -X POST http://localhost:8001/services \
  --data name=product-service \
  --data url=http://host.docker.internal:8081

curl -i -X POST http://localhost:8001/services/product-service/routes \
  --data paths[]=/products \
  --data strip_path=false

# Configure Category Service Route
curl -i -X POST http://localhost:8001/services \
  --data name=category-service \
  --data url=http://host.docker.internal:8082

curl -i -X POST http://localhost:8001/services/category-service/routes \
  --data paths[]=/categories \
  --data strip_path=false

# Access APIs via Kong (Port 8000)
# Products: http://localhost:8000/api/products
# Categories: http://localhost:8000/api/categories