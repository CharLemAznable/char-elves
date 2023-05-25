package com.github.charlemaznable.core.kafka;

import com.ctrip.framework.apollo.ConfigService;
import com.github.charlemaznable.etcdconf.EtcdConfigService;
import lombok.NoArgsConstructor;
import org.n3r.diamond.client.Miner;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class KafkaConfigElf {

    public static final String KAFKA_CONFIG_APOLLO_NAMESPACE = "KafkaConfig";
    public static final String KAFKA_CONFIG_DIAMOND_GROUP_NAME = "KafkaConfig";
    public static final String KAFKA_CONFIG_ETCD_NAMESPACE = "KafkaConfig";

    public static String getApolloProperty(String propertyName) {
        return ConfigService.getConfig(KAFKA_CONFIG_APOLLO_NAMESPACE)
                .getProperty(propertyName, "");
    }

    public static String getDiamondStone(String dataId) {
        return new Miner().getStone(KAFKA_CONFIG_DIAMOND_GROUP_NAME, dataId);
    }

    public static String getEtcdValue(String key) {
        return EtcdConfigService.getConfig(KAFKA_CONFIG_ETCD_NAMESPACE).getString(key, "");
    }
}
