package io.quarkiverse.quarkus.wasm.runtime.sdk;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record WasmResponseContext(Map<String, List<String>> headers) implements WasmContext {
    public static WasmResponseContext empty() {
        return new WasmResponseContext(Collections.emptyMap());
    }

    public static WasmResponseContext ofHeaders(Map<String, List<String>> headers) {
        return new WasmResponseContext(new HashMap<>(headers));
    }
}
