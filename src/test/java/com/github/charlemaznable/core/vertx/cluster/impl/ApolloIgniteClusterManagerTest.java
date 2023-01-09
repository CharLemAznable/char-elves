package com.github.charlemaznable.core.vertx.cluster.impl;

import com.github.charlemaznable.apollo.MockApolloServer;
import com.github.charlemaznable.core.vertx.cluster.ApolloPropertyTest;
import com.github.charlemaznable.core.vertx.cluster.IgniteClusterManagerTest;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ApolloIgniteClusterManagerTest
        extends IgniteClusterManagerTest implements ApolloPropertyTest {

    @Test
    public void testApolloIgniteClusterManager() {
        MockApolloServer.setUpMockServer();

        val ignite = parseApolloProperty("ignite");
        assertIgnite(ignite);

        val ignite0 = parseApolloProperty("ignite0");
        assertIgnite0(ignite0);

        val ignite1 = parseApolloProperty("ignite1");
        assertIgnite1(ignite1);

        MockApolloServer.tearDownMockServer();
    }

    @Test
    public void testApolloIgniteClusterManagerError() {
        MockApolloServer.setUpMockServer();

        val igniteNone = parseApolloProperty("igniteNone");
        assertIgniteNone(igniteNone);

        val igniteError = parseApolloProperty("igniteError");
        assertIgniteError(igniteError);

        MockApolloServer.tearDownMockServer();
    }
}
