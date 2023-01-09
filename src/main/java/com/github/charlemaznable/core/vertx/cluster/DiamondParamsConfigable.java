package com.github.charlemaznable.core.vertx.cluster;

import lombok.val;

import static com.github.charlemaznable.core.vertx.VertxClusterConfigElf.fetchVertxClusterConfigApplyParams;
import static com.github.charlemaznable.core.vertx.VertxClusterConfigElf.getDiamondStone;
import static java.util.Objects.isNull;

public interface DiamondParamsConfigable extends ParamsConfigable {

    @Override
    default String fetchConfigValue(String[] params) {
        val applyParams = fetchVertxClusterConfigApplyParams(params);
        if (isNull(applyParams)) return null;
        return getDiamondStone(applyParams.getLeft(), applyParams.getRight());
    }
}
