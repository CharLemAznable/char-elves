package com.github.charlemaznable.core.vertx.cluster.impl;

import com.github.charlemaznable.core.vertx.cluster.EtcdValueTest;
import com.github.charlemaznable.core.vertx.cluster.ZookeeperClusterManagerTest;
import com.github.charlemaznable.etcdconf.EtcdConfigService;
import com.github.charlemaznable.etcdconf.test.EmbeddedEtcdCluster;
import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.vertx.VertxClusterConfigElf.VERTX_CLUSTER_CONFIG_ETCD_NAMESPACE;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.VERTX_OPTIONS_ETCD_NAMESPACE;

public class EtcdZookeeperClusterManagerTest
        extends ZookeeperClusterManagerTest implements EtcdValueTest {

    @Test
    public void testEtcdZookeeperClusterManager() {
        EtcdConfigService.setUpTestMode();

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "zookeeper", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdZookeeperClusterManager");
        val zookeeper = parseEtcdValue("zookeeper");
        assertZookeeper(zookeeper);

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "zookeeper0", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdZookeeperClusterManager()");
        val zookeeper0 = parseEtcdValue("zookeeper0");
        assertZookeeper0(zookeeper0);

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "zookeeper1", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdZookeeperClusterManager(zookeeperJson)");
        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_CLUSTER_CONFIG_ETCD_NAMESPACE, "zookeeperJson", CUSTOM_ZOOKEEPER_JSON);
        val zookeeper1 = parseEtcdValue("zookeeper1");
        assertZookeeper1(zookeeper1);

        EtcdConfigService.tearDownTestMode();
    }

    @Test
    public void testEtcdZookeeperClusterManagerError() {
        EtcdConfigService.setUpTestMode();

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "zookeeperNone", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdZookeeperClusterManager(zookeeperNotExists)");
        val zookeeperNone = parseEtcdValue("zookeeperNone");
        assertZookeeperNone(zookeeperNone);

        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "zookeeperError", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdZookeeperClusterManager(zookeeperJsonError)");
        EmbeddedEtcdCluster.addOrModifyProperty(VERTX_CLUSTER_CONFIG_ETCD_NAMESPACE, "zookeeperJsonError", """
                {
                  "zookeeperHosts":"127.0.0.1",
                  "sessionTimeout":20000,
                  "connectTimeout":3000,
                  "rootPath":"io.vertx",
                  "retry":\s
                    "initialSleepTime":100,
                    "intervalTimes":10000,
                    "maxTimes":5
                }""");
        val zookeeperError = parseEtcdValue("zookeeperError");
        assertZookeeperError(zookeeperError);

        EtcdConfigService.tearDownTestMode();
    }
}
