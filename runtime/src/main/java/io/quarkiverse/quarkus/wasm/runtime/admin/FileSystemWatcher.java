package io.quarkiverse.quarkus.wasm.runtime.admin;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.quarkiverse.quarkus.wasm.runtime.config.FilterChainConfig;
import io.quarkiverse.quarkus.wasm.runtime.config.ImmutableFilterChainConfig;
import io.quarkiverse.quarkus.wasm.runtime.config.MutableFilterChainConfig;
import io.quarkus.logging.Log;

public class FileSystemWatcher {
    ObjectMapper mapper;
    Path path;
    WatchService watcher;
    WatchKey watchKey;

    public FileSystemWatcher() {
        try {
            this.mapper = new ObjectMapper(new YAMLFactory());
            this.path = Path.of(System.getProperty("user.dir")).resolve("config");
            this.watcher = FileSystems.getDefault().newWatchService();
            this.watchKey = path.register(watcher,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException("cannot watch file system", e);
        }
    }

    public FilterChainConfig reloadConfig() {
        boolean reload = false;
        for (WatchEvent<?> event : watchKey.pollEvents()) {
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path f = ev.context();
            if (f.endsWith(Path.of("application.yaml"))) {
                reload = true;
            }
        }

        if (!reload) {
            return null;
        }

        try {
            byte[] bytes = Files.readAllBytes(path.resolve("application.yaml"));
            JsonNode root = mapper.readTree(bytes);
            JsonNode filterChainRoot = root.get("quarkus").get("wasm").get("filter-chain");
            MutableFilterChainConfig mutableFilterChainConfig = mapper.treeToValue(filterChainRoot,
                    MutableFilterChainConfig.class);
            var cfg = new ImmutableFilterChainConfig(mutableFilterChainConfig);
            Log.infof("File system watcher reloaded the config.", new String(bytes));
            Log.infof("File system watcher reloaded the config.", mutableFilterChainConfig);
            return cfg;
        } catch (IOException e) {
            Log.error(e);
            return null;
        }
    }

}
