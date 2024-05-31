package io.quarkiverse.quarkus.wasm.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class WasmTest {

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Test
    public void testGreeting() {
        given().contentType("application/json")
                .body("HELLO").log().headers()
                .when().post("/greeting").then()
                .statusCode(200).log().headers()
                .contentType("application/json")
                .body("count", equalTo(2));
    }
}
