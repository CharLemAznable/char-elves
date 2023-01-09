package com.github.charlemaznable.core.vertx;

import com.ctrip.framework.apollo.ConfigService;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.n3r.diamond.client.Miner;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

@NoArgsConstructor(access = PRIVATE)
public final class VertxClusterConfigElf {

    public static final String VERTX_CLUSTER_CONFIG_APOLLO_NAMESPACE = "VertxClusterConfig";
    public static final String VERTX_CLUSTER_CONFIG_DIAMOND_GROUP_NAME = "VertxClusterConfig";

    public static String getApolloProperty(String namespace, String propertyName) {
        return ConfigService.getConfig(defaultIfBlank(namespace,
                VERTX_CLUSTER_CONFIG_APOLLO_NAMESPACE)).getProperty(propertyName, "");
    }

    public static String getDiamondStone(String group, String dataId) {
        return new Miner().getStone(defaultIfBlank(group, VERTX_CLUSTER_CONFIG_DIAMOND_GROUP_NAME), dataId);
    }

    public static Pair<String, String> fetchVertxClusterConfigApplyParams(String[] params) {
        String param0 = null;
        String param1 = null;

        if (params.length == 2) {
            // @ParamsAppliable(param0, param1)
            param0 = params[0];
            param1 = params[1];
        } else if (params.length == 1) {
            // @ParamsAppliable(param1)
            param1 = params[0];
        }
        // @ParamsAppliable() or error params
        if (isBlank(param1)) return null;

        return Pair.of(param0, param1);
    }
}
