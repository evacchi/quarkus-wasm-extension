package io.quarkiverse.quarkus.wasm.deployment;

import io.quarkiverse.quarkus.wasm.runtime.WasmExtensionServlet;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.undertow.deployment.ServletBuildItem;

class WasmProcessor {

    private static final String FEATURE = "wasm";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    ServletBuildItem createServlet() {
        ServletBuildItem servletBuildItem = ServletBuildItem
                .builder("greeting-extension", WasmExtensionServlet.class.getName())
                .addMapping("/greeting")
                .build();
        return servletBuildItem;
    }

}
