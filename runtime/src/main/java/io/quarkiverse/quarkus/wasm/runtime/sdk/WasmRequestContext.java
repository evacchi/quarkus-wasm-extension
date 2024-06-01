package io.quarkiverse.quarkus.wasm.runtime.sdk;

import java.util.*;

public record WasmRequestContext(Map<String, String> headers) {
    public static WasmRequestContext empty() {
        return new WasmRequestContext(Collections.emptyMap());
    }

    public static WasmRequestContext ofHeaders(Iterable<Map.Entry<String, List<String>>> entries) {
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : entries) {
            for (String h : entry.getValue()) {
                headers.put(entry.getKey(), h);
            }
        }
        return new WasmRequestContext(headers);
    }
}
