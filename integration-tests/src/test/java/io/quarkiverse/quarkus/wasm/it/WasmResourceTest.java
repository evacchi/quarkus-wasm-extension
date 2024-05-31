package io.quarkiverse.quarkus.wasm.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class WasmResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/wasm")
                .then()
                .statusCode(200)
                .body(is("Hello wasm"));
    }
}
