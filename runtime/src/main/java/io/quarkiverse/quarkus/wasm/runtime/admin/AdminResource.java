package io.quarkiverse.quarkus.wasm.runtime.admin;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.ArrayList;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import io.quarkiverse.quarkus.wasm.runtime.config.*;

@Path("/wasm/admin")
@ApplicationScoped
public class AdminResource {

    @Inject
    FilterChainConfig initialConfig;

    MutableFilterChainConfig currentConfig;

    @Inject
    Event<ConfigChanged> configChangedEvent;

    @PostConstruct
    public void initConfig() {
        var plugins = initialConfig.plugins();
        var ps = new ArrayList<Plugin>();
        for (var p : plugins) {
            ps.add(new Plugin(p));
        }
        currentConfig = new MutableFilterChainConfig(ps);
    }

    @PATCH
    @Path("/config")
    @Consumes(APPLICATION_JSON)
    public void update(MutableFilterChainConfig cfg) {
        currentConfig.setPlugins(cfg.getPlugins());
        configChangedEvent.fire(new ConfigChanged(new ImmutableFilterChainConfig(currentConfig)));
    }

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    public MutableFilterChainConfig currentConfig() {
        return currentConfig;
    }
}
