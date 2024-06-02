package io.quarkiverse.quarkus.wasm.runtime.config;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class MutableFilterChainConfig {
    List<Plugin> plugins = List.of();

    public MutableFilterChainConfig() {
    }

    public MutableFilterChainConfig(List<Plugin> plugins) {
        setPlugins(plugins);
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }

}
