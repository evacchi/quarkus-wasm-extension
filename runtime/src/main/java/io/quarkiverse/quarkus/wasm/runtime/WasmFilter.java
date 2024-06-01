package io.quarkiverse.quarkus.wasm.runtime;

import org.extism.sdk.ExtismException;
import org.extism.sdk.Plugin;

/**
 * The implementation of a WasmFilter.
 */
public class WasmFilter implements AutoCloseable {
    private final Plugin plugin;

    public WasmFilter(Plugin plugin) {
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
}
