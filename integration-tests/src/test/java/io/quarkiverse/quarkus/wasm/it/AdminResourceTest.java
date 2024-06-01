package io.quarkiverse.quarkus.wasm.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AdminResourceTest {


    @Test
    public void testCurrentConfigEndpoint() {
        given()
                .when().get("/wasm/admin/config")
                .then()
                .statusCode(200)
                .body("plugins[0].name", equalTo("hello-headers"));
    }

    @Test
    public void testUpdateConfigEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                            {
                                "plugins": [
                                    {"name": "hello-headers-2"}
                                ]
                            }
                        """)
                .when().patch("/wasm/admin/config")
                .then()
                .statusCode(204);

        given()
                .when().get("/wasm/admin/config")
                .then()
                .statusCode(200)
                .body("plugins[0].name", equalTo("hello-headers-2"));

    }


    @Test
    public void testE2eUpdateConfigEndpoint() {

        given()
                .when().get("/wasm")
                .then()
                .statusCode(200)
                .body(is("Hello wasm"));

        given()
                .contentType(ContentType.JSON)
                .body("""
                            {
                                "plugins": [
                                    {"name": "hello-headers-2"}
                                ]
                            }
                        """)
                .when().patch("/wasm/admin/config")
                .then()
                .statusCode(204);

        given()
                .when().get("/wasm/admin/config")
                .then()
                .statusCode(200)
                .body("plugins[0].name", equalTo("hello-headers-2"));

        given()
                .when().get("/wasm")
                .then()
                .statusCode(200)
                .body(not("Hello wasm"));


    }

}
