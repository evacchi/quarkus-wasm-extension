package io.quarkiverse.quarkus.wasm.runtime;

import java.io.IOException;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import io.quarkiverse.quarkus.wasm.runtime.admin.FileSystemWatcher;
import io.quarkiverse.quarkus.wasm.runtime.config.ConfigChanged;
import io.quarkiverse.quarkus.wasm.runtime.config.FilterChainConfig;
import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmRequestContext;
import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmResponseContext;
import io.quarkus.logging.Log;

@ApplicationScoped
public class RequestFilter {
    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

    FileSystemWatcher fileSystemWatcher;

    @Inject
    FilterChainProvider filterChainProvider;

    @Inject
    FilterChainConfig cfg;

    @ConfigProperty(name = "quarkus.wasm.file-watcher.enabled", defaultValue = "false")
    boolean fileWatcher;

    FilterChain filterChain;

    @PostConstruct
    public void loadFilterChain() throws Exception {
        this.filterChain = filterChainProvider.createFromConfig(cfg);
        if (fileWatcher) {
            this.fileSystemWatcher = new FileSystemWatcher();
        }
    }

    @ServerRequestFilter(preMatching = true)
    public Response requestFilter(ContainerRequestContext requestContext) {
        this.checkConfig();
        try {
            var ctx = WasmRequestContext.ofHeaders(requestContext.getHeaders());
            var resCtx = filterChain.onRequestHeaders(ctx);
            requestContext.getHeaders().putAll(resCtx.headers());
            var status = resCtx.status();
            if (status != null && (status.code() == 500 || status.code() == 403)) {
                return Response.status(status.code())
                        .entity(status.message())
                        .build();
            } else {
                return null;
            }
        } catch (WasmFilterException e) {
            Log.error("An exception was caught", e);
            return Response.serverError()
                    .entity("An error occurred while pre-processing the request")
                    .build();
        }
    }

    @ServerResponseFilter
    public void responseFilter(ContainerResponseContext responseContext) {
        try {
            var ctx = WasmResponseContext.ofHeaders(responseContext.getStringHeaders());
            var resCtx = filterChain.onResponseHeaders(ctx);
            for (var e : resCtx.headers().entrySet()) {
                MultivaluedMap<String, Object> headers = responseContext.getHeaders();
                List<Object> values = headers.get(e.getKey());
                List<String> candidate = e.getValue();
                if (values == null || !values.contains(candidate)) {
                    headers.add(e.getKey(), candidate);
                }
            }

        } catch (WasmFilterException e) {
            responseContext.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            responseContext.setEntity("An error occurred while post-processing the response");
        }
    }

    private void checkConfig() {
        if (!fileWatcher) {
            return;
        }
        try {
            var updatedConfig = fileSystemWatcher.reloadConfig();
            if (updatedConfig != null) {
                this.cfg = updatedConfig;
                this.filterChain = filterChainProvider.createFromConfig(cfg);
                Log.infof("Filter chain was reloaded: %s", cfg);
            }
        } catch (IOException e) {
            Log.error("Error while reloading config", e);
        }
    }

    public void onConfigChange(@Observes ConfigChanged evt) throws Exception {
        LOG.info("omg config has changed: " + evt);
        this.cfg = evt.cfg();
        this.filterChain = filterChainProvider.createFromConfig(cfg);
    }

}
