package io.quarkiverse.quarkus.wasm.runtime;

import org.extism.sdk.ExtismException;

public class WasmFilterException extends RuntimeException {
    public WasmFilterException(String message) {
        super(message);
    }

    public WasmFilterException(ExtismException e) {
        super(e);
    }
}
