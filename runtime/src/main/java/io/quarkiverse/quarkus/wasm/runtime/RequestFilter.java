package io.quarkiverse.quarkus.wasm.runtime;

import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmRequestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

@ApplicationScoped
public class RequestFilter {
    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

    @Inject
    FilterChain filterChain;

    @ServerRequestFilter(preMatching = true)
    public void requestFilter(ContainerRequestContext requestContext) {
        try {
            var ctx = WasmRequestContext.ofHeaders(requestContext.getHeaders());
            var resCtx = filterChain.invoke(ctx);
            requestContext.getHeaders().putAll(resCtx.headers());
        } catch (WasmFilterException e) {
            requestContext.abortWith(
                    Response.serverError()
                            .entity("An error occurred while pre-processing the request")
                            .build());
        }
    }

}
