package io.quarkiverse.quarkus.wasm.runtime.sdk;

import java.util.*;

public record WasmRequestContext(Map<String, List<String>> headers) {
    public static WasmRequestContext empty() {
        return new WasmRequestContext(Collections.emptyMap());
    }

    public static WasmRequestContext ofHeaders(Map<String, List<String>> headers) {
        return new WasmRequestContext(new HashMap<>(headers));
    }
}
