package com.github.charlemaznable.core.vertx.cluster.impl;

import com.github.charlemaznable.core.vertx.cluster.EtcdValueTest;
import com.github.charlemaznable.core.vertx.cluster.IgniteClusterManagerTest;
import com.github.charlemaznable.etcdconf.EtcdConfigService;
import com.github.charlemaznable.etcdconf.test.EmbeddedEtcdCluster;
import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.vertx.VertxClusterConfigElf.VERTX_CLUSTER_CONFIG_ETCD_NAMESPACE;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.VERTX_OPTIONS_ETCD_NAMESPACE;

public class EtcdIgniteClusterManagerTest
        extends IgniteClusterManagerTest implements EtcdValueTest {

    @Test
    public void testEtcdIgniteClusterManager() {
        EtcdConfigService.setUpTestMode();

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "ignite", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdIgniteClusterManager");
        val ignite = parseEtcdValue("ignite");
        assertIgnite(ignite);

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "ignite0", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdIgniteClusterManager()");
        val ignite0 = parseEtcdValue("ignite0");
        assertIgnite0(ignite0);

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "ignite1", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdIgniteClusterManager(igniteJson)");
        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_CLUSTER_CONFIG_ETCD_NAMESPACE, "igniteJson", """
                {
                  "localPort":47101
                }""");
        val ignite1 = parseEtcdValue("ignite1");
        assertIgnite1(ignite1);

        EtcdConfigService.tearDownTestMode();
    }

    @Test
    public void testEtcdIgniteClusterManagerError() {
        EtcdConfigService.setUpTestMode();

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "igniteNone", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdIgniteClusterManager(igniteNotExists)");
        val igniteNone = parseEtcdValue("igniteNone");
        assertIgniteNone(igniteNone);

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "igniteError", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdIgniteClusterManager(igniteJsonError)");
        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_CLUSTER_CONFIG_ETCD_NAMESPACE, "igniteJsonError", """
                {
                  "localPort":47101,
                  "discoveryOptions":
                }""");
        val igniteError = parseEtcdValue("igniteError");
        assertIgniteError(igniteError);

        EtcdConfigService.tearDownTestMode();
    }
}
