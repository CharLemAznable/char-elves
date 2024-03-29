package com.github.charlemaznable.core.vertx.spring;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.impl.clustered.ClusteredEventBus;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringJUnitConfig(CustomOptionsConfiguration.class)
public class CustomOptionsTest {

    @Autowired
    private Vertx vertx;
    @Autowired
    private io.vertx.rxjava.core.Vertx rxVertx;
    @Autowired
    private io.vertx.reactivex.core.Vertx rx2Vertx;
    @Autowired
    private io.vertx.rxjava3.core.Vertx rx3Vertx;

    @Test
    public void testSpringVertxConfiguration() {
        assertNotNull(vertx);
        val reflectVertx = on(vertx);
        int defaultWorkerPoolSize = reflectVertx.field("defaultWorkerPoolSize").get();
        assertEquals(CustomOptionsConfiguration.DEFAULT_WORKER_POOL_SIZE, defaultWorkerPoolSize);
        val eventBus = reflectVertx.field("eventBus").get();
        assertTrue(eventBus instanceof ClusteredEventBus);

        assertNotNull(rxVertx);
        assertNotNull(rx2Vertx);
        assertNotNull(rx3Vertx);
    }
}
