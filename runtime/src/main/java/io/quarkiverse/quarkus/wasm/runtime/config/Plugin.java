package io.quarkiverse.quarkus.wasm.runtime.config;

public class Plugin {
    private String name;

    private String type;

    public Plugin(FilterChainConfig.Plugin plugin) {
        this.name = plugin.name();
        this.type = plugin.type();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}