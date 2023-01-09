package com.github.charlemaznable.core.vertx.cluster;

import com.hazelcast.config.ConfigBuilder;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.config.YamlConfigBuilder;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

@Slf4j
public abstract class ConfigableHazelcastClusterManager
        extends HazelcastClusterManager implements ParamsConfigable {

    @Override
    public void applyConfigValue(String configValue) {
        // read config value as XML or YAML format
        val inputStream = new ByteArrayInputStream(configValue.getBytes(UTF_8));
        ConfigBuilder configBuilder = startsWithIgnoreCase(configValue, "<?xml ")
                ? new XmlConfigBuilder(inputStream) : new YamlConfigBuilder(inputStream);
        try {
            this.setConfig(configBuilder.build());
        } catch (Exception e) {
            log.error("Failed to set config", e);
        }
    }
}
