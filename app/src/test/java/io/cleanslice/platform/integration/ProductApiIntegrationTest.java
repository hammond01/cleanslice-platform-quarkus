package io.cleanslice.platform.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "RUN_DB_INTEGRATION_TESTS", matches = "true")
class ProductApiIntegrationTest {

    @Test
    void healthEndpoint_shouldBeUp() {
        given()
                .when().get("/q/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void createAndListProduct_shouldPersistAndReturnData() {
        String productName = "IT-" + UUID.randomUUID();

        Map<String, Object> payload = Map.of(
                "name", productName,
                "description", "integration-test-product",
                "price", new BigDecimal("12.50"),
                "stock", 10,
                "categoryId", 1L
        );

        given()
                .contentType("application/json")
                .body(payload)
                .when().post("/api/v1/products")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.name", equalTo(productName));

        String listResponse = given()
                .when().get("/api/v1/products")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .asString();

        JsonPath jsonPath = new JsonPath(listResponse);
        assertTrue(jsonPath.getList("data.name", String.class).contains(productName));
    }
}
