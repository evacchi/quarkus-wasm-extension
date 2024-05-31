package io.quarkiverse.quarkus.wasm.runtime;

import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;

public class RequestFilter {
    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    FilterChain filterChain;

    @RouteFilter
    void requestFilter(RoutingContext rc) throws Exception {
        var ctx = WasmRequestContext.ofHeaders(rc.request().headers().entries());
        byte[] bytes = mapper.writeValueAsBytes(ctx);

        for (WasmFilter plugin : filterChain.plugins()) {
            byte[] res = plugin.invoke(bytes);
            var resCtx = mapper.readValue(res, WasmRequestContext.class);
            rc.request().headers().setAll(resCtx.headers());
        }

        rc.next();
    }

}
