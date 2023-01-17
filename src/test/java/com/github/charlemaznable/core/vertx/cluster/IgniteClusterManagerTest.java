package com.github.charlemaznable.core.vertx.cluster;

import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import io.vertx.spi.cluster.ignite.IgniteOptions;
import lombok.val;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class IgniteClusterManagerTest {

    protected void assertIgnite(VertxOptions ignite) {
        val igniteClusterManager = ignite.getClusterManager();
        assertTrue(igniteClusterManager instanceof IgniteClusterManager);
        assertNull(on(igniteClusterManager).get("extOptions"));
    }

    protected void assertIgnite0(VertxOptions ignite0) {
        val ignite0ClusterManager = ignite0.getClusterManager();
        assertTrue(ignite0ClusterManager instanceof IgniteClusterManager);
        assertNull(on(ignite0ClusterManager).get("extOptions"));
    }

    protected void assertIgnite1(VertxOptions ignite1) {
        val ignite1ClusterManager = ignite1.getClusterManager();
        assertTrue(ignite1ClusterManager instanceof IgniteClusterManager);
        IgniteOptions extOptions = on(ignite1ClusterManager).get("extOptions");
        assertNotNull(extOptions);
        assertEquals(47101, extOptions.getLocalPort());
    }

    protected void assertIgniteNone(VertxOptions igniteNone) {
        val igniteNoneClusterManager = igniteNone.getClusterManager();
        assertTrue(igniteNoneClusterManager instanceof IgniteClusterManager);
        assertNull(on(igniteNoneClusterManager).get("extOptions"));
    }

    protected void assertIgniteError(VertxOptions igniteError) {
        val igniteErrorClusterManager = igniteError.getClusterManager();
        assertTrue(igniteErrorClusterManager instanceof IgniteClusterManager);
        assertNull(on(igniteErrorClusterManager).get("extOptions"));
    }
}
