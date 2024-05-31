package io.quarkiverse.quarkus.wasm.runtime;

public class FilterPlugin {
    String name;

    public FilterPlugin(FilterChainConfig.Plugin pluginCfg) {
        this.name = pluginCfg.name();
    }

    public String name() {
        return name;
    }
}