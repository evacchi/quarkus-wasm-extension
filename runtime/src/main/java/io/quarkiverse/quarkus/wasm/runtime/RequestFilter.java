package io.quarkiverse.quarkus.wasm.runtime;

import io.quarkiverse.quarkus.wasm.runtime.admin.FileSystemWatcher;
import io.quarkiverse.quarkus.wasm.runtime.config.FilterChainConfig;
import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmContext;
import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmRequestContext;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

@ApplicationScoped
public class RequestFilter {
    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

//    FileSystemWatcher fileSystemWatcher;

//    @Inject
//    FilterChainProvider filterChainProvider;

    @Inject
    FilterChainConfig cfg;

    @ConfigProperty(name = "quarkus.wasm.file-watcher.enabled", defaultValue = "false")
    boolean fileWatcher;

    @Inject
    FilterChain filterChain;

//    @PostConstruct
//    public void loadFilterChain() throws Exception {
//        this.filterChain = filterChainProvider.createFromConfig(cfg);
//    }

    @ServerRequestFilter(preMatching = true)
    public Response requestFilter(ContainerRequestContext requestContext) {
        var headers = requestContext.getHeaders();
        var ctx = WasmRequestContext.ofHeaders(headers);
        try {
            ctx = filterChain.onRequestHeaders(ctx);
            requestContext.getHeaders().putAll(ctx.headers());
            return null;
        } catch (WasmFilterException e) {
            Log.error("An exception was caught will pre-processing a request", e);
            return Response.serverError()
                    .entity("An error occurred while pre-processing the request")
                    .build();
        }
    }

    @ServerResponseFilter
    public void responseFilter(ContainerResponseContext responseContext) {
    }

}
