package io.quarkiverse.quarkus.wasm.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

public class WasmInputBuildItem extends SimpleBuildItem {
    private final byte[] data;

    public WasmInputBuildItem(byte[] data) {
        this.data = data;
    }

    public byte[] data() {
        return data;
    }
}
