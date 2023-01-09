package com.github.charlemaznable.core.vertx.cluster;

import io.vertx.core.VertxOptions;

import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static com.github.charlemaznable.core.vertx.VertxElf.parsePropertiesToVertxOptions;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.getApolloProperty;

public interface ApolloPropertyTest {

    default VertxOptions parseApolloProperty(String propertyName) {
        return parsePropertiesToVertxOptions(
                parseStringToProperties(getApolloProperty(propertyName)));
    }
}
