package com.github.charlemaznable.core.vertx.cluster;

import io.vertx.core.VertxOptions;

import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static com.github.charlemaznable.core.vertx.VertxElf.parsePropertiesToVertxOptions;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.getDiamondStone;

public interface DiamondStoneTest {

    default VertxOptions parseDiamondStone(String dataId) {
        return parsePropertiesToVertxOptions(
                parseStringToProperties(getDiamondStone(dataId)));
    }
}
