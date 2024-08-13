package io.quarkiverse.quarkus.wasm.runtime;

public class WasmFilterException extends RuntimeException {
    public WasmFilterException(String message) {
        super(message);
    }

    public WasmFilterException(Exception e) {
        super(e);
    }
}
