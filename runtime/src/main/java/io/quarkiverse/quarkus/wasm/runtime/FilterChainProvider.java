package io.quarkiverse.quarkus.wasm.runtime;

import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.extism.sdk.Plugin;
import org.extism.sdk.manifest.Manifest;
import org.extism.sdk.wasm.WasmSourceResolver;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.quarkus.wasm.runtime.config.FilterChainConfig;

@ApplicationScoped
public class FilterChainProvider {
    private static final Logger LOG = Logger.getLogger(FilterChainProvider.class);

    @Inject
    ObjectMapper mapper;

    @Produces
    public FilterChain filterChain(FilterChainConfig cfg) throws Exception {
        WasmSourceResolver wasmSourceResolver = new WasmSourceResolver();
        var plugins = new ArrayList<WasmFilter>();
        for (var plugin : cfg.plugins()) {
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

        return new FilterChain(mapper, plugins);
    }
}
