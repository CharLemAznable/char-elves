package com.github.charlemaznable.core.vertx.cluster;

import io.vertx.core.json.JsonObject;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ConfigableZookeeperClusterManager
        extends ZookeeperClusterManager implements ParamsConfigable {

    @Override
    public void applyConfigValue(String configValue) {
        try {
            this.setConfig(new JsonObject(configValue));
        } catch (Exception e) {
            log.error("Failed to set config", e);
        }
    }
}
