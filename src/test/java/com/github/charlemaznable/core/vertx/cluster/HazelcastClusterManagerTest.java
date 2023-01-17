package com.github.charlemaznable.core.vertx.cluster;

import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.val;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class HazelcastClusterManagerTest {

    protected void assertHazelcast(VertxOptions hazelcast) {
        val hazelcastClusterManager = hazelcast.getClusterManager();
        assertTrue(hazelcastClusterManager instanceof HazelcastClusterManager);
        assertNull(((HazelcastClusterManager) hazelcastClusterManager).getConfig());
    }

    protected void assertHazelcast0(VertxOptions hazelcast0) {
        val hazelcast0ClusterManager = hazelcast0.getClusterManager();
        assertTrue(hazelcast0ClusterManager instanceof HazelcastClusterManager);
        assertNull(((HazelcastClusterManager) hazelcast0ClusterManager).getConfig());
    }

    protected void assertHazelcast1(VertxOptions hazelcast1) {
        val hazelcast1ClusterManager = hazelcast1.getClusterManager();
        assertTrue(hazelcast1ClusterManager instanceof HazelcastClusterManager);
        val configXml = ((HazelcastClusterManager) hazelcast1ClusterManager).getConfig();
        assertNotNull(configXml);
        assertEquals(6801, configXml.getNetworkConfig().getPort());
    }

    protected void assertHazelcast2(VertxOptions hazelcast2) {
        val hazelcast2ClusterManager = hazelcast2.getClusterManager();
        assertTrue(hazelcast2ClusterManager instanceof HazelcastClusterManager);
        val configYaml = ((HazelcastClusterManager) hazelcast2ClusterManager).getConfig();
        assertNotNull(configYaml);
        assertEquals(7901, configYaml.getNetworkConfig().getPort());
    }

    protected void assertHazelcastNone(VertxOptions hazelcastNone) {
        val hazelcastNoneClusterManager = hazelcastNone.getClusterManager();
        assertTrue(hazelcastNoneClusterManager instanceof HazelcastClusterManager);
        assertNull(((HazelcastClusterManager) hazelcastNoneClusterManager).getConfig());
    }

    protected void assertHazelcastError(VertxOptions hazelcastError) {
        val hazelcastErrorClusterManager = hazelcastError.getClusterManager();
        assertTrue(hazelcastErrorClusterManager instanceof HazelcastClusterManager);
        assertNull(((HazelcastClusterManager) hazelcastErrorClusterManager).getConfig());
    }
}
