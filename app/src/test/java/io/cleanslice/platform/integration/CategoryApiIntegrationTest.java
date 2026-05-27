package io.cleanslice.platform.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "RUN_DB_INTEGRATION_TESTS", matches = "true")
class CategoryApiIntegrationTest {

    @Test
    void createAndListCategory_shouldPersistAndReturnData() {
        String categoryName = "CAT-IT-" + UUID.randomUUID();

        Map<String, Object> payload = Map.of(
                "name", categoryName,
                "description", "integration-test-category",
                "parentId", 1L,
                "slug", "cat-it-" + UUID.randomUUID()
        );

        given()
                .contentType("application/json")
                .body(payload)
                .when().post("/api/v1/categories")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.name", equalTo(categoryName));

        String listResponse = given()
                .when().get("/api/v1/categories")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .asString();

        JsonPath jsonPath = new JsonPath(listResponse);
        assertTrue(jsonPath.getList("data.name", String.class).contains(categoryName));
    }
}
