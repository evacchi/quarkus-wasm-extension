package io.quarkiverse.quarkus.wasm.runtime.sdk;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record WasmRequestContext(Map<String, List<String>> headers) implements WasmContext {
    public static WasmRequestContext empty() {
        return new WasmRequestContext(Collections.emptyMap());
    }

    public static WasmRequestContext ofHeaders(Map<String, List<String>> headers) {
        return new WasmRequestContext(new HashMap<>(headers));
    }
}
