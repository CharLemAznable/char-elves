package com.github.charlemaznable.core.vertx.cluster.impl;

import com.github.charlemaznable.apollo.MockApolloServer;
import com.github.charlemaznable.core.vertx.cluster.ApolloPropertyTest;
import com.github.charlemaznable.core.vertx.cluster.ZookeeperClusterManagerTest;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ApolloZookeeperClusterManagerTest
        extends ZookeeperClusterManagerTest implements ApolloPropertyTest {

    @Test
    public void testApolloZookeeperClusterManager() {
        MockApolloServer.setUpMockServer();

        val zookeeper = parseApolloProperty("zookeeper");
        assertZookeeper(zookeeper);

        val zookeeper0 = parseApolloProperty("zookeeper0");
        assertZookeeper0(zookeeper0);

        val zookeeper1 = parseApolloProperty("zookeeper1");
        assertZookeeper1(zookeeper1);

        MockApolloServer.tearDownMockServer();
    }

    @Test
    public void testApolloZookeeperClusterManagerError() {
        MockApolloServer.setUpMockServer();

        val zookeeperNone = parseApolloProperty("zookeeperNone");
        assertZookeeperNone(zookeeperNone);

        val zookeeperError = parseApolloProperty("zookeeperError");
        assertZookeeperError(zookeeperError);

        MockApolloServer.tearDownMockServer();
    }
}
