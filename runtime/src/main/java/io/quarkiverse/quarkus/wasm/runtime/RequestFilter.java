package io.quarkiverse.quarkus.wasm.runtime;

import io.quarkus.vertx.web.RouteFilter;
import io.smallrye.common.annotation.Blocking;
import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.container.ContainerRequestContext;

import jakarta.ws.rs.core.Request;
import org.extism.sdk.Plugin;
import org.extism.sdk.manifest.Manifest;
import org.extism.sdk.wasm.UrlWasmSource;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class RequestFilter {

    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

    @RouteFilter
    void myFilter(RoutingContext rc) {
        var url = "https://github.com/extism/plugins/releases/latest/download/count_vowels.wasm";
        var manifest = new Manifest(List.of(UrlWasmSource.fromUrl(url)));


        try (var plugin = new Plugin(manifest, false, null)) {
            String ct = rc.request().headers().get("Content-Type");
            String res = plugin.call("count_vowels", ct);
            rc.request().headers().set("X-Count-Vowels", res);
        }
        rc.next();
    }

}
