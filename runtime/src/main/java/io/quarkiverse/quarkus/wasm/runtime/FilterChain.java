package io.quarkiverse.quarkus.wasm.runtime;

import java.io.IOException;
import java.util.Collection;

import org.extism.chicory.sdk.ExtismException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmRequestContext;
import io.quarkiverse.quarkus.wasm.runtime.sdk.WasmResponseContext;
import io.quarkus.logging.Log;

public final class FilterChain {
    private final ObjectMapper mapper;
    private final Collection<WasmFilter> plugins;

    public FilterChain(ObjectMapper mapper, Collection<WasmFilter> plugins) {
        this.mapper = mapper;
        this.plugins = plugins;
    }

    public WasmRequestContext onRequestHeaders(WasmRequestContext ctx) {
        try {
            for (WasmFilter plugin : plugins) {
                Log.infof("Invoking plugin %s", plugin.name());
                byte[] inBytes = mapper.writeValueAsBytes(ctx);
                byte[] outBytes = plugin.onRequestHeaders(inBytes);
                ctx = mapper.readValue(outBytes, WasmRequestContext.class);
            }
            return ctx;
        } catch (IOException | ExtismException e) {
            throw new RuntimeException(e);
        }
    }

    public WasmResponseContext onResponseHeaders(WasmResponseContext ctx) {
        try {
            for (WasmFilter plugin : plugins) {
                Log.infof("Invoking plugin %s", plugin.name());
                byte[] inBytes = mapper.writeValueAsBytes(ctx);
                byte[] outBytes = plugin.onResponseHeaders(inBytes);
                ctx = mapper.readValue(outBytes, WasmResponseContext.class);
            }
            return ctx;
        } catch (IOException | ExtismException e) {
            throw new RuntimeException(e);
        }
    }

}
