package io.quarkiverse.quarkus.wasm.runtime;

import org.extism.sdk.Plugin;

public class WasmFilter implements AutoCloseable {
    private final Plugin plugin;

    public WasmFilter(Plugin plugin) {
        this.plugin = plugin;
    }

    public byte[] invoke(byte[] bytes) {
        return plugin.call("count_vowels", bytes);
    }

    @Override
    public void close() throws Exception {
        plugin.close();
    }
}
