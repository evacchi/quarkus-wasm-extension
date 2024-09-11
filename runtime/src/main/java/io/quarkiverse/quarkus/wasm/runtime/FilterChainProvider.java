package io.quarkiverse.quarkus.wasm.runtime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.extism.chicory.sdk.Manifest;
import org.extism.chicory.sdk.ManifestWasm;
import org.extism.chicory.sdk.Plugin;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.quarkus.wasm.runtime.config.FilterChainConfig;

@ApplicationScoped
public class FilterChainProvider {
    private static final Logger LOG = Logger.getLogger(FilterChainProvider.class);

    @Inject
    ObjectMapper mapper;

    Path configPath = Path.of(System.getProperty("user.dir")).resolve("config");

    public FilterChain createFromConfig(FilterChainConfig cfg) throws IOException {
        var plugins = new ArrayList<WasmFilter>();
        for (var plugin : cfg.plugins()) {
            WasmFilter wasmFilter;
            switch (plugin.type()) {
                case "filesystem":
                    wasmFilter = loadFromFileSystem(plugin);
                    break;
                case "resource":
                default:
                    wasmFilter = loadFromResource(plugin);
            }
            plugins.add(wasmFilter);
        }

        return new FilterChain(mapper, plugins);
    }

    private WasmFilter loadFromFileSystem(FilterChainConfig.Plugin plugin)
            throws IOException {
        var wasmPath = ManifestWasm.fromFilePath(configPath.resolve(plugin.name() + ".wasm")).build();
        LOG.infof("Loading from file system: %s", wasmPath);

        var wasmPlugin = Plugin.ofManifest(Manifest.ofWasms(wasmPath).build()).build();
        return new WasmFilter(plugin.name(), wasmPlugin);
    }

    private WasmFilter loadFromResource(FilterChainConfig.Plugin plugin)
            throws IOException {
        String pluginPath = "/" + plugin.name() + ".wasm";
        try (var wasmStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pluginPath)) {
            if (wasmStream == null) {
                throw new WasmFilterCreationException("Cannot load plugin " + plugin.name());
            }
            Path tempFile = Files.createTempFile("chicory-temp", plugin.name());
            Files.write(tempFile, wasmStream.readAllBytes());
            var manifest = Manifest.ofWasms(ManifestWasm.fromFilePath(tempFile).build()).build();
            var wasmPlugin = Plugin.ofManifest(manifest).build();

            return new WasmFilter(plugin.name(), wasmPlugin);
        }
    }
}
