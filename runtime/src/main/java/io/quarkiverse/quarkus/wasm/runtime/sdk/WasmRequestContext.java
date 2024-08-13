package io.quarkiverse.quarkus.wasm.runtime.sdk;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonSerialize
public record WasmRequestContext(Map<String, List<String>> headers, Status status) implements WasmContext {

    public static WasmRequestContext empty() {
        return new WasmRequestContext(Collections.emptyMap(), null);
    }

    public static WasmRequestContext ofHeaders(Map<String, List<String>> headers) {
        return new WasmRequestContext(new HashMap<>(headers), null);
    }

    @RegisterForReflection
    @JsonSerialize
    public record Status(int code, String message) {
    }
}
