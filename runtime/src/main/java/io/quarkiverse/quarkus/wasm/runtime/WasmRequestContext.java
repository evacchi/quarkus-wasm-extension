package io.quarkiverse.quarkus.wasm.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public record WasmRequestContext(Map<String, String> headers) {
    public static WasmRequestContext ofHeaders(Collection<Map.Entry<String, String>> entries) {
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, String> entry : entries) {
            headers.put(entry.getKey(), entry.getValue());
        }
        return new WasmRequestContext(headers);
    }
}
