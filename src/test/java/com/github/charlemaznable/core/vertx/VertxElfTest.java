package com.github.charlemaznable.core.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static com.github.charlemaznable.core.vertx.VertxElf.buildVertx;
import static com.github.charlemaznable.core.vertx.VertxElf.closeVertx;
import static com.github.charlemaznable.core.vertx.VertxElf.closeVertxImmediately;
import static com.github.charlemaznable.core.vertx.VertxElf.parsePropertiesToVertxOptions;
import static com.google.common.collect.Lists.newArrayList;
import static org.joor.Reflect.onClass;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
public class VertxElfTest {

    @Test
    public void testParseVertxOptions() {
        val propertiesString = """
                eventLoopPoolSize=2
                maxEventLoopExecuteTime=5
                haEnabled=true
                haGroup=___DEFAULT___
                maxEventLoopExecuteTimeUnit=SECONDS
                blockedThreadCheckIntervalUnit=SECOND
                """;
        val properties = parseStringToProperties(propertiesString);
        val vertxOptions = parsePropertiesToVertxOptions(properties);
        assertEquals(2, vertxOptions.getEventLoopPoolSize());
        assertEquals(5, vertxOptions.getMaxEventLoopExecuteTime());
        assertTrue(vertxOptions.isHAEnabled());
        assertEquals("___DEFAULT___", vertxOptions.getHAGroup());
        assertEquals(TimeUnit.SECONDS, vertxOptions.getMaxEventLoopExecuteTimeUnit());
        assertNull(vertxOptions.getBlockedThreadCheckIntervalUnit()); // error config SECOND, should be SECONDS
        assertNull(vertxOptions.getClusterManager());
    }

    @Test
    public void testVertxBuildAndClose() {
        val vertx1 = buildVertx(new VertxOptions());
        assertFalse(vertx1.isClustered());
        closeVertxImmediately(vertx1);

        val clusterOptions = new VertxOptions();
        clusterOptions.setClusterManager(new HazelcastClusterManager());
        val vertx2 = buildVertx(clusterOptions, throwable -> null);
        assertTrue(vertx2.isClustered());
        closeVertx(vertx2, throwable -> null);

        val vertx3 = buildVertx(clusterOptions);
        assertTrue(vertx3.isClustered());
        closeVertx(vertx3);

        assertDoesNotThrow(() -> closeVertxImmediately(null));
        assertDoesNotThrow(() -> closeVertx(null));
    }

    @Test
    public void testVertxExecuteBlocking(Vertx vertx, VertxTestContext testContext) {
        assertDoesNotThrow(() -> onClass(VertxElf.class).create().get());

        vertx.deployVerticle(new TestVerticle(),
                testContext.succeeding(id -> testContext.completeNow()));
    }

    public static class TestVerticle extends AbstractVerticle {

        @Override
        public void start(Promise<Void> startPromise) {
            Future.all(newArrayList(
                    Future.<Void>future(f ->
                            VertxElf.<Void>executeBlocking(() -> {
                                throw new UnsupportedOperationException();
                            }, asyncResult -> {
                                assertTrue(asyncResult.failed());
                                assertTrue(asyncResult.cause() instanceof UnsupportedOperationException);
                                f.complete();
                            })
                    ),
                    Future.<Void>future(f ->
                            VertxElf.<Void>executeBlocking(() -> null,
                                    asyncResult -> {
                                        assertTrue(asyncResult.succeeded());
                                        assertNull(asyncResult.result());
                                        f.complete();
                                    })
                    )
            )).onComplete(asyncResult -> {
                if (asyncResult.failed()) {
                    startPromise.fail(asyncResult.cause());
                } else {
                    startPromise.complete();
                }
            });
        }
    }
}
