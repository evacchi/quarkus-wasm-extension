package io.quarkiverse.quarkus.wasm.runtime;

import org.extism.sdk.ExtismException;
import org.extism.sdk.Plugin;

/**
 * The implementation of a WasmFilter.
 */
public class WasmFilter implements AutoCloseable {
    private final String name;
    private final Plugin plugin;

    public WasmFilter(String name, Plugin plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    public byte[] invoke(byte[] bytes) {
        try {
            return plugin.call("request_headers", bytes);
        } catch (ExtismException e) {
            throw new WasmFilterException(e);
        }
    }

    @Override
    public void close() throws Exception {
        plugin.close();
    }

    @Override
    public String toString() {
        return "WasmFilter{" +
                "name='" + name + '\'' +
                '}';
    }

    public String name() {
        return name;
    }
}
