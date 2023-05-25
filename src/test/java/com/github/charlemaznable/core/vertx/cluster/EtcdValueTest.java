package com.github.charlemaznable.core.vertx.cluster;

import io.vertx.core.VertxOptions;

import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static com.github.charlemaznable.core.vertx.VertxElf.parsePropertiesToVertxOptions;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.getEtcdValue;

public interface EtcdValueTest {

    default VertxOptions parseEtcdValue(String key) {
        return parsePropertiesToVertxOptions(
                parseStringToProperties(getEtcdValue(key)));
    }
}
