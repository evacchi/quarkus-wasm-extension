package io.quarkiverse.quarkus.wasm.runtime.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImmutableFilterChainConfig implements FilterChainConfig {
    List<Plugin> plugins;

    public ImmutableFilterChainConfig(MutableFilterChainConfig cfg) {
        var l = new ArrayList<Plugin>();
        for (var plugin : cfg.getPlugins()) {
            l.add(new Plugin(plugin.getName(), plugin.getType()));
        }
        plugins = l;
    }

    @Override
    public List<FilterChainConfig.Plugin> plugins() {
        return Collections.unmodifiableList(plugins);
    }

    static record Plugin(String name, String type) implements FilterChainConfig.Plugin {
    }

    @Override
    public String toString() {
        return "ImmutableFilterChainConfig{" +
                "plugins=" + plugins +
                '}';
    }
}
