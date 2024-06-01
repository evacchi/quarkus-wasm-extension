package io.quarkiverse.quarkus.wasm.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.io.InputStream;
import java.net.URI;
import java.util.*;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;

import org.junit.jupiter.api.Test;

import io.quarkiverse.quarkus.wasm.runtime.RequestFilter;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class WasmResourceTest {

    @Inject
    RequestFilter requestFilter;

    @Test
    public void testFilterChain() {
        requestFilter.requestFilter(new MockWasmRequestContext());
    }

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/wasm")
                .then()
                .statusCode(200)
                .body(is("Hello wasm"));
    }
}

class MockWasmRequestContext implements ContainerRequestContext {

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public Collection<String> getPropertyNames() {
        return List.of();
    }

    @Override
    public void setProperty(String name, Object object) {

    }

    @Override
    public void removeProperty(String name) {

    }

    @Override
    public UriInfo getUriInfo() {
        return null;
    }

    @Override
    public void setRequestUri(URI requestUri) {

    }

    @Override
    public void setRequestUri(URI baseUri, URI requestUri) {

    }

    @Override
    public Request getRequest() {
        return null;
    }

    @Override
    public String getMethod() {
        return "";
    }

    @Override
    public void setMethod(String method) {

    }

    @Override
    public MultivaluedMap<String, String> getHeaders() {
        return new MultivaluedHashMap<>();
    }

    @Override
    public String getHeaderString(String name) {
        return "";
    }

    @Override
    public Date getDate() {
        return null;
    }

    @Override
    public Locale getLanguage() {
        return null;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        return List.of();
    }

    @Override
    public List<Locale> getAcceptableLanguages() {
        return List.of();
    }

    @Override
    public Map<String, Cookie> getCookies() {
        return Map.of();
    }

    @Override
    public boolean hasEntity() {
        return false;
    }

    @Override
    public InputStream getEntityStream() {
        return null;
    }

    @Override
    public void setEntityStream(InputStream input) {

    }

    @Override
    public SecurityContext getSecurityContext() {
        return null;
    }

    @Override
    public void setSecurityContext(SecurityContext context) {

    }

    @Override
    public void abortWith(Response response) {

    }
}
