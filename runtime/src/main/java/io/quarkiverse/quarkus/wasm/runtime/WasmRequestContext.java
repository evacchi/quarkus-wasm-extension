package io.quarkiverse.quarkus.wasm.runtime;

import java.util.Map;

public record WasmRequestContext(Map<String, String> headers) {
}
