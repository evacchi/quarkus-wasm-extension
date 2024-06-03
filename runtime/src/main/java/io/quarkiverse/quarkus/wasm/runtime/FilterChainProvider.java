package io.quarkiverse.quarkus.wasm.runtime;

import com.dylibso.chicory.runtime.HostFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.quarkus.wasm.runtime.config.FilterChainConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.extism.chicory.sdk.Manifest;
import org.extism.chicory.sdk.Plugin;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@ApplicationScoped
public class FilterChainProvider {
    private static final Logger LOG = Logger.getLogger(FilterChainProvider.class);

    @Inject
    ObjectMapper mapper;

    Path configPath = Path.of(System.getProperty("user.dir")).resolve("config");

    @Produces
    public FilterChain createFromConfig(FilterChainConfig cfg) throws IOException {
        var plugins = new ArrayList<WasmFilter>();
        for (var plugin : cfg.plugins()) {
            WasmFilter wasmFilter;
            switch (plugin.type()) {
                case "resource":
                default:
                    wasmFilter = loadFromResource(plugin);
            }
            plugins.add(wasmFilter);
        }

        return new FilterChain(mapper, plugins);
    }

    private WasmFilter loadFromResource(FilterChainConfig.Plugin plugin) throws IOException {
        var pluginPath = "/" + plugin.name() + ".wasm";
        try (var wasmStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pluginPath)) {
            if (wasmStream == null) {
                throw new WasmFilterCreationException("Cannot load plugin " + plugin.name());
            }
            var manifest = Manifest.fromBytes(plugin.name(), wasmStream.readAllBytes());
            return new WasmFilter(plugin.name(), new Plugin(manifest, new HostFunction[0], null));
        }
    }

    private WasmFilter loadFromFileSystem(FilterChainConfig.Plugin plugin)
            throws IOException {
        Path wasmPath = configPath.resolve(plugin.name() + ".wasm");
        LOG.infof("Loading from file system: %s", wasmPath);
        var manifest = Manifest.fromFilePath(wasmPath);
        return new WasmFilter(plugin.name(), new Plugin(manifest, new HostFunction[0], null));
    }

}
