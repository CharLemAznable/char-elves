package com.github.charlemaznable.core.vertx.cluster;

import io.vertx.core.json.JsonObject;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import io.vertx.spi.cluster.ignite.IgniteOptions;
import lombok.extern.slf4j.Slf4j;

import static org.joor.Reflect.on;

@Slf4j
public abstract class ConfigableIgniteClusterManager
        extends IgniteClusterManager implements ParamsConfigable {

    @Override
    public void applyConfigValue(String configValue) {
        try {
            on(this).set("extOptions", new IgniteOptions(new JsonObject(configValue)));
        } catch (Exception e) {
            log.error("Failed to set config", e);
        }
    }
}
