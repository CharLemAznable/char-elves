package com.github.charlemaznable.core.vertx.cluster.impl;

import com.github.charlemaznable.core.vertx.cluster.DiamondStoneTest;
import com.github.charlemaznable.core.vertx.cluster.ZookeeperClusterManagerTest;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.n3r.diamond.client.impl.MockDiamondServer;

import static com.github.charlemaznable.core.vertx.VertxClusterConfigElf.VERTX_CLUSTER_CONFIG_DIAMOND_GROUP_NAME;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.VERTX_OPTIONS_DIAMOND_GROUP_NAME;

public class DiamondZookeeperClusterManagerTest
        extends ZookeeperClusterManagerTest implements DiamondStoneTest {

    @Test
    public void testDiamondZookeeperClusterManager() {
        MockDiamondServer.setUpMockServer();

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "zookeeper", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondZookeeperClusterManager");
        val zookeeper = parseDiamondStone("zookeeper");
        assertZookeeper(zookeeper);

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "zookeeper0", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondZookeeperClusterManager()");
        val zookeeper0 = parseDiamondStone("zookeeper0");
        assertZookeeper0(zookeeper0);

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "zookeeper1", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondZookeeperClusterManager(zookeeperJson)");
        MockDiamondServer.setConfigInfo(VERTX_CLUSTER_CONFIG_DIAMOND_GROUP_NAME, "zookeeperJson", CUSTOM_ZOOKEEPER_JSON);
        val zookeeper1 = parseDiamondStone("zookeeper1");
        assertZookeeper1(zookeeper1);

        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testDiamondZookeeperClusterManagerError() {
        MockDiamondServer.setUpMockServer();

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "zookeeperNone", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondZookeeperClusterManager(zookeeperNotExists)");
        val zookeeperNone = parseDiamondStone("zookeeperNone");
        assertZookeeperNone(zookeeperNone);

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "zookeeperError", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondZookeeperClusterManager(zookeeperJsonError)");
        MockDiamondServer.setConfigInfo(VERTX_CLUSTER_CONFIG_DIAMOND_GROUP_NAME, "zookeeperJsonError", "" +
                "{\n" +
                "  \"zookeeperHosts\":\"127.0.0.1\",\n" +
                "  \"sessionTimeout\":20000,\n" +
                "  \"connectTimeout\":3000,\n" +
                "  \"rootPath\":\"io.vertx\",\n" +
                "  \"retry\": \n" +
                "    \"initialSleepTime\":100,\n" +
                "    \"intervalTimes\":10000,\n" +
                "    \"maxTimes\":5\n" +
                "}");
        val zookeeperError = parseDiamondStone("zookeeperError");
        assertZookeeperError(zookeeperError);

        MockDiamondServer.tearDownMockServer();
    }
}
