package io.quarkiverse.quarkus.wasm.runtime;

import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmRequestContext;

@ApplicationScoped
public class RequestFilter {
    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

    @Inject
    FilterChain filterChain;

    @ServerRequestFilter(preMatching = true)
    public void requestFilter(ContainerRequestContext requestContext) throws Exception {
        var ctx = WasmRequestContext.ofHeaders(requestContext.getHeaders().entrySet());
        var resCtx = filterChain.invoke(ctx);
        for (Map.Entry<String, String> h : resCtx.headers().entrySet()) {
            requestContext.getHeaders().putSingle(h.getKey(), h.getValue());
        }
    }

}
