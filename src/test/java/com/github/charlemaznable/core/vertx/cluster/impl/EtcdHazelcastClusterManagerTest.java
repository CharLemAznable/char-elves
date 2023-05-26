package com.github.charlemaznable.core.vertx.cluster.impl;

import com.github.charlemaznable.core.vertx.cluster.EtcdValueTest;
import com.github.charlemaznable.core.vertx.cluster.HazelcastClusterManagerTest;
import com.github.charlemaznable.etcdconf.MockEtcdServer;
import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.vertx.VertxClusterConfigElf.VERTX_CLUSTER_CONFIG_ETCD_NAMESPACE;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.VERTX_OPTIONS_ETCD_NAMESPACE;

public class EtcdHazelcastClusterManagerTest
        extends HazelcastClusterManagerTest implements EtcdValueTest {

    @Test
    public void testEtcdHazelcastClusterManager() {
        MockEtcdServer.setUpMockServer();

        MockEtcdServer.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "hazelcast", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdHazelcastClusterManager");
        val hazelcast = parseEtcdValue("hazelcast");
        assertHazelcast(hazelcast);

        MockEtcdServer.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "hazelcast0", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdHazelcastClusterManager()");
        val hazelcast0 = parseEtcdValue("hazelcast0");
        assertHazelcast0(hazelcast0);

        MockEtcdServer.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "hazelcast1", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdHazelcastClusterManager(hazelcastXml)");
        MockEtcdServer.addOrModifyProperty(VERTX_CLUSTER_CONFIG_ETCD_NAMESPACE, "hazelcastXml", """
                <?xml version="1.0" encoding="UTF-8"?>
                <hazelcast xmlns="http://www.hazelcast.com/schema/config"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="http://www.hazelcast.com/schema/config
                           http://www.hazelcast.com/schema/config/hazelcast-config-4.0.xsd">
                    <network>
                        <port>6801</port>
                    </network>
                </hazelcast>
                """);
        val hazelcast1 = parseEtcdValue("hazelcast1");
        assertHazelcast1(hazelcast1);

        MockEtcdServer.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "hazelcast2", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdHazelcastClusterManager(VertxClusters, hazelcastYaml)");
        MockEtcdServer.addOrModifyProperty("VertxClusters", "hazelcastYaml", """
                hazelcast:
                  network:
                    port:
                      port: 7901
                """);
        val hazelcast2 = parseEtcdValue("hazelcast2");
        assertHazelcast2(hazelcast2);

        MockEtcdServer.tearDownMockServer();
    }

    @Test
    public void testEtcdHazelcastClusterManagerError() {
        MockEtcdServer.setUpMockServer();

        MockEtcdServer.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "hazelcastNone", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdHazelcastClusterManager(hazelcastNotExists)");
        val hazelcastNone = parseEtcdValue("hazelcastNone");
        assertHazelcastNone(hazelcastNone);

        MockEtcdServer.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "hazelcastError", "" +
                "clusterManager=@com.github.charlemaznable.core.vertx.cluster.impl.EtcdHazelcastClusterManager(hazelcastXmlError)");
        MockEtcdServer.addOrModifyProperty(VERTX_CLUSTER_CONFIG_ETCD_NAMESPACE, "hazelcastXmlError", """
                <?xml version="1.0" encoding="UTF-8"?>
                <hazelcast xmlns="http://www.hazelcast.com/schema/config"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="http://www.hazelcast.com/schema/config
                           http://www.hazelcast.com/schema/config/hazelcast-config-4.0.xsd">
                    <network>
                        <port>6801
                    </network>
                </hazelcast>
                """);
        val hazelcastError = parseEtcdValue("hazelcastError");
        assertHazelcastError(hazelcastError);

        MockEtcdServer.tearDownMockServer();
    }
}
