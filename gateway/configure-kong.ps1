# Configure Kong Gateway for HONEY_BEE microservices

Write-Host "Configuring Kong Gateway..." -ForegroundColor Green

# Product Service
Write-Host "`nSetting up Product Service..." -ForegroundColor Yellow
curl -i -X POST http://localhost:8001/services `
  --data "name=product-service" `
  --data "url=http://localhost:8012"

curl -i -X POST http://localhost:8001/services/product-service/routes `
  --data "paths[]=/api/products" `
  --data "strip_path=false"

# Category Service
Write-Host "`nSetting up Category Service..." -ForegroundColor Yellow
curl -i -X POST http://localhost:8001/services `
  --data "name=category-service" `
  --data "url=http://localhost:8013"

curl -i -X POST http://localhost:8001/services/category-service/routes `
  --data "paths[]=/api/categories" `
  --data "strip_path=false"

# Audit Service
Write-Host "`nSetting up Audit Service..." -ForegroundColor Yellow
curl -i -X POST http://localhost:8001/services `
  --data "name=audit-service" `
  --data "url=http://localhost:8011"

curl -i -X POST http://localhost:8001/services/audit-service/routes `
  --data "paths[]=/api/audit" `
  --data "strip_path=false"

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "Kong Gateway Configuration Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "`nAccess via Kong Gateway (Port 8010):" -ForegroundColor Cyan
Write-Host "  Products:   http://localhost:8010/api/products" -ForegroundColor White
Write-Host "  Categories: http://localhost:8010/api/categories" -ForegroundColor White
Write-Host "  Audit:      http://localhost:8010/api/audit" -ForegroundColor White
Write-Host "`nDirect Access (for testing):" -ForegroundColor Cyan
Write-Host "  Product:  http://localhost:8012/api/products" -ForegroundColor White
Write-Host "  Category: http://localhost:8013/api/categories" -ForegroundColor White
Write-Host "  Audit:    http://localhost:8011/api/audit" -ForegroundColor White
Write-Host "`nSwagger UI:" -ForegroundColor Cyan
Write-Host "  Product:  http://localhost:8012/swagger-ui/" -ForegroundColor White
Write-Host "  Category: http://localhost:8013/swagger-ui/" -ForegroundColor White
Write-Host "  Audit:    http://localhost:8011/swagger-ui/" -ForegroundColor White
