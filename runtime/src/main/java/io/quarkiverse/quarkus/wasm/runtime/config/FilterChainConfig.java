package io.quarkiverse.quarkus.wasm.runtime.config;

import java.util.List;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "quarkus.wasm.filter-chain")
public interface FilterChainConfig {
    /**
     * @return the list of plugins to load.
     */
    List<Plugin> plugins();

    /**
     * Plugin config.
     */
    interface Plugin {
        /**
         * @return name of the plugin.
         */
        String name();
    }
}
