package io.quarkiverse.quarkus.wasm.runtime.sdk;

import java.util.List;
import java.util.Map;

public interface WasmContext {
    Map<String, List<String>> headers();
}
