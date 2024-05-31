package io.quarkiverse.quarkus.wasm.runtime;

import java.util.Collection;

public record FilterChain(Collection<FilterPlugin> plugins) {
}
