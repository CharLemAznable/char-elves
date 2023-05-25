package com.github.charlemaznable.core.vertx.cluster;

import lombok.val;

import static com.github.charlemaznable.core.vertx.VertxClusterConfigElf.fetchVertxClusterConfigApplyParams;
import static com.github.charlemaznable.core.vertx.VertxClusterConfigElf.getEtcdValue;
import static java.util.Objects.isNull;

public interface EtcdParamsConfigable extends ParamsConfigable {

    @Override
    default String fetchConfigValue(String[] params) {
        val applyParams = fetchVertxClusterConfigApplyParams(params);
        if (isNull(applyParams)) return null;
        return getEtcdValue(applyParams.getLeft(), applyParams.getRight());
    }
}
