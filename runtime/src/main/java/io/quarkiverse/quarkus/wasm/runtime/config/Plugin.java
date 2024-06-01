package io.quarkiverse.quarkus.wasm.runtime.config;

public class Plugin {
    private String name;

    public Plugin(FilterChainConfig.Plugin plugin) {
        this.name = plugin.name();
    }

    public Plugin() {
    }

    public Plugin(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}