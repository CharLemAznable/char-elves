package com.github.charlemaznable.core.vertx;

import com.ctrip.framework.apollo.ConfigService;
import com.github.charlemaznable.etcdconf.EtcdConfigService;
import lombok.NoArgsConstructor;
import org.n3r.diamond.client.Miner;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class VertxOptionsConfigElf {

    public static final String VERTX_OPTIONS_APOLLO_NAMESPACE = "VertxOptions";
    public static final String VERTX_OPTIONS_DIAMOND_GROUP_NAME = "VertxOptions";
    public static final String VERTX_OPTIONS_ETCD_NAMESPACE = "VertxOptions";

    public static String getApolloProperty(String propertyName) {
        return ConfigService.getConfig(VERTX_OPTIONS_APOLLO_NAMESPACE)
                .getProperty(propertyName, "");
    }

    public static String getDiamondStone(String dataId) {
        return new Miner().getStone(VERTX_OPTIONS_DIAMOND_GROUP_NAME, dataId);
    }

    public static String getEtcdValue(String key) {
        return EtcdConfigService.getConfig(VERTX_OPTIONS_ETCD_NAMESPACE).getString(key, "");
    }
}
