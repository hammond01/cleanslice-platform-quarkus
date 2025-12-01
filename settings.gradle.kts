rootProject.name = "honey-bee"

include("services:product-service")
project(":services:product-service").projectDir = file("services/product-service")

include("services:category-service")
project(":services:category-service").projectDir = file("services/category-service")

include("services:audit-service")
project(":services:audit-service").projectDir = file("services/audit-service")

include("saga-orchestration")
