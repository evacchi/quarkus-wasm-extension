package io.quarkiverse.quarkus.wasm.runtime;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;

import org.extism.sdk.Plugin;
import org.extism.sdk.manifest.Manifest;
import org.extism.sdk.wasm.WasmSourceResolver;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;

public class RequestFilter {

    private static final Logger LOG = Logger.getLogger(RequestFilter.class);

    @Inject
    FilterChain filterChain;

    @Inject
    ObjectMapper mapper;

    WasmSourceResolver wasmSourceResolver = new WasmSourceResolver();

    @RouteFilter
    void requestFilter(RoutingContext rc) throws Exception {
        var plugins = new ArrayList<WasmFilter>();
        for (FilterPlugin plugin : filterChain.plugins()) {
            String pluginPath = "/" + plugin.name() + ".wasm";
            LOG.info(pluginPath);
            var wasmStream = this.getClass()
                    .getResourceAsStream(pluginPath);
            var wasmSource = wasmSourceResolver.resolve(plugin.name(),
                    wasmStream.readAllBytes());
            var manifest = new Manifest(wasmSource);
            var filter = new WasmFilter(new Plugin(manifest, true, null));
            plugins.add(filter);
        }

        for (Map.Entry<String, String> header : rc.request().headers()) {
            LOG.info(header);
        }

        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, String> entry : rc.request().headers().entries()) {
            headers.put(entry.getKey(), entry.getValue());
        }
        var ctx = new WasmRequestContext(headers);
        byte[] bytes = mapper.writeValueAsBytes(ctx);

        for (WasmFilter plugin : plugins) {
            byte[] res = plugin.invoke(bytes);
            rc.request().headers().set("X-Count-Vowels", new String(res, StandardCharsets.UTF_8));
        }

        rc.next();
    }

}
