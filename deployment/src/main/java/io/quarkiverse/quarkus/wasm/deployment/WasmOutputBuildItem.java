package io.quarkiverse.quarkus.wasm.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

public class WasmOutputBuildItem extends SimpleBuildItem {
    private final byte[] data;

    public WasmOutputBuildItem(byte[] data) {
        this.data = data;
    }

}
