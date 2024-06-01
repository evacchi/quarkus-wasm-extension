package io.quarkiverse.quarkus.wasm.runtime;

import java.io.IOException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import io.quarkiverse.quarkus.wasm.runtime.admin.FileSystemWatcher;
import io.quarkiverse.quarkus.wasm.runtime.config.ConfigChanged;
import io.quarkiverse.quarkus.wasm.runtime.config.FilterChainConfig;
import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmRequestContext;
import io.quarkus.logging.Log;

@ApplicationScoped
public class RequestFilter {
    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

    FileSystemWatcher fileSystemWatcher;

    @Inject
    FilterChainProvider filterChainProvider;

    @Inject
    FilterChainConfig cfg;

    FilterChain filterChain;

    @PostConstruct
    public void loadFilterChain() throws Exception {
        this.filterChain = filterChainProvider.createFromConfig(cfg);
        this.fileSystemWatcher = new FileSystemWatcher();
    }

    @ServerRequestFilter(preMatching = true)
    public void requestFilter(ContainerRequestContext requestContext) {
        this.checkConfig();
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

    private void checkConfig() {
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
