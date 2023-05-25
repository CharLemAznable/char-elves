package com.github.charlemaznable.core.es;

import com.ctrip.framework.apollo.ConfigService;
import com.github.charlemaznable.etcdconf.EtcdConfigService;
import lombok.NoArgsConstructor;
import org.n3r.diamond.client.Miner;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EsConfigElf {

    public static final String ES_CONFIG_APOLLO_NAMESPACE = "EsConfig";
    public static final String ES_CONFIG_DIAMOND_GROUP_NAME = "EsConfig";
    public static final String ES_CONFIG_ETCD_NAMESPACE = "EsConfig";

    public static String getApolloProperty(String propertyName) {
        return ConfigService.getConfig(ES_CONFIG_APOLLO_NAMESPACE)
                .getProperty(propertyName, "");
    }

    public static String getDiamondStone(String dataId) {
        return new Miner().getStone(ES_CONFIG_DIAMOND_GROUP_NAME, dataId);
    }

    public static String getEtcdValue(String key) {
        return EtcdConfigService.getConfig(ES_CONFIG_ETCD_NAMESPACE).getString(key, "");
    }
}
