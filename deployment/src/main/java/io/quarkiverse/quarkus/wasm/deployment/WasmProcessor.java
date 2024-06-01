package io.quarkiverse.quarkus.wasm.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;

class WasmProcessor {

    private static final String FEATURE = "wasm";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerWasmResources(BuildProducer<NativeImageResourceBuildItem> nativeResources) {
        // todo
    }

}
