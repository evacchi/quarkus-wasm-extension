package io.quarkiverse.quarkus.wasm.runtime;

import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class FilterChainProvider {

    @Produces
    FilterChain loadFilterChain(FilterChainConfig cfg) {
        var l = new ArrayList<FilterPlugin>(cfg.plugins().size());
        for (FilterChainConfig.Plugin plugin : cfg.plugins()) {
            l.add(new FilterPlugin(plugin));
        }
        return new FilterChain(l);
    }

}
