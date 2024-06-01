package io.quarkiverse.quarkus.wasm.runtime;

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.microprofileext.config.event.ChangeEvent;

import io.quarkiverse.quarkus.wasm.runtime.config.FilterChainConfig;
import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmRequestContext;

@ApplicationScoped
public class RequestFilter {
    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

    @Inject
    FilterChainProvider filterChainProvider;

    //@ConfigProperty(name = "quarkus.wasm.filter-chain")
    @Inject
    FilterChainConfig cfg;

    @ConfigProperty(name = "quarkus.wasm.filter-chain.flatplugins")
    List<String> plugins;

    FilterChain filterChain;

    @PostConstruct
    public void loadFilterChain() throws Exception {
        this.filterChain = filterChainProvider.createFromConfig(cfg);
    }

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

    public void onConfigChange(@Observes ChangeEvent evt) {
        LOG.info("omg config has changed: " + evt);
    }

}
