package com.github.charlemaznable.core.vertx.cluster.impl;

import com.github.charlemaznable.core.vertx.cluster.DiamondStoneTest;
import com.github.charlemaznable.core.vertx.cluster.IgniteClusterManagerTest;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.n3r.diamond.client.impl.MockDiamondServer;

import static com.github.charlemaznable.core.vertx.VertxClusterConfigElf.VERTX_CLUSTER_CONFIG_DIAMOND_GROUP_NAME;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.VERTX_OPTIONS_DIAMOND_GROUP_NAME;

public class DiamondIgniteClusterManagerTest
        extends IgniteClusterManagerTest implements DiamondStoneTest {

    @Test
    public void testDiamondIgniteClusterManager() {
        MockDiamondServer.setUpMockServer();

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "ignite", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondIgniteClusterManager");
        val ignite = parseDiamondStone("ignite");
        assertIgnite(ignite);

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "ignite0", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondIgniteClusterManager()");
        val ignite0 = parseDiamondStone("ignite0");
        assertIgnite0(ignite0);

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "ignite1", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondIgniteClusterManager(igniteJson)");
        MockDiamondServer.setConfigInfo(VERTX_CLUSTER_CONFIG_DIAMOND_GROUP_NAME, "igniteJson", """
                {
                  "localPort":47101
                }""");
        val ignite1 = parseDiamondStone("ignite1");
        assertIgnite1(ignite1);

        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testDiamondIgniteClusterManagerError() {
        MockDiamondServer.setUpMockServer();

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "igniteNone", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondIgniteClusterManager(igniteNotExists)");
        val igniteNone = parseDiamondStone("igniteNone");
        assertIgniteNone(igniteNone);

        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "igniteError", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.DiamondIgniteClusterManager(igniteJsonError)");
        MockDiamondServer.setConfigInfo(VERTX_CLUSTER_CONFIG_DIAMOND_GROUP_NAME, "igniteJsonError", """
                {
                  "localPort":47101,
                  "discoveryOptions":
                }""");
        val igniteError = parseDiamondStone("igniteError");
        assertIgniteError(igniteError);

        MockDiamondServer.tearDownMockServer();
    }
}
