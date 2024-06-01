package io.quarkiverse.quarkus.wasm.runtime;

import io.quarkiverse.quarkus.wasm.runtime.config.ConfigChanged;
import io.quarkiverse.quarkus.wasm.runtime.config.FilterChainConfig;
import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmRequestContext;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

@ApplicationScoped
public class RequestFilter {
    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

    @Inject
    FilterChainProvider filterChainProvider;

    @Inject
    FilterChainConfig cfg;

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

    public void onConfigChange(@Observes ConfigChanged evt) throws Exception {
        LOG.info("omg config has changed: " + evt);
        this.cfg = evt.cfg();
        this.filterChain = filterChainProvider.createFromConfig(cfg);
    }

}
