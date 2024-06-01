package io.quarkiverse.quarkus.wasm.runtime;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
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

    public FilterChain createFromConfig(FilterChainConfig cfg) throws IOException {
        WasmSourceResolver wasmSourceResolver = new WasmSourceResolver();
        var plugins = new ArrayList<WasmFilter>();
        for (var plugin : cfg.plugins()) {
            String pluginPath = "/" + plugin.name() + ".wasm";
            try (var wasmStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pluginPath)) {
                if (wasmStream == null) {
                    throw new WasmFilterCreationException("Cannot load plugin " + plugin.name());
                }
                var wasmSource = wasmSourceResolver.resolve(plugin.name(), wasmStream.readAllBytes());
                var manifest = new Manifest(wasmSource);
                var filter = new WasmFilter(plugin.name(), new Plugin(manifest, true, null));
                plugins.add(filter);
            }
        }

        return new FilterChain(mapper, plugins);
    }
}
