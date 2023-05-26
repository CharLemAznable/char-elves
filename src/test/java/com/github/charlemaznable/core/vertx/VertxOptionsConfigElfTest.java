package com.github.charlemaznable.core.vertx;

import com.github.charlemaznable.apollo.MockApolloServer;
import com.github.charlemaznable.etcdconf.MockEtcdServer;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.n3r.diamond.client.impl.MockDiamondServer;

import java.util.concurrent.TimeUnit;

import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static com.github.charlemaznable.core.vertx.VertxElf.parsePropertiesToVertxOptions;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.VERTX_OPTIONS_DIAMOND_GROUP_NAME;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.VERTX_OPTIONS_ETCD_NAMESPACE;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.getApolloProperty;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.getDiamondStone;
import static com.github.charlemaznable.core.vertx.VertxOptionsConfigElf.getEtcdValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VertxOptionsConfigElfTest {

    @Test
    public void testVertxOptionsConfigElfInApollo() {
        MockApolloServer.setUpMockServer();

        val configProperty = getApolloProperty("default");
        assertConfigValue(configProperty);

        MockApolloServer.tearDownMockServer();
    }

    @Test
    public void testVertxOptionsConfigElfInDiamond() {
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo(VERTX_OPTIONS_DIAMOND_GROUP_NAME, "DEFAULT", """
                eventLoopPoolSize=2
                maxEventLoopExecuteTime=5
                haEnabled=true
                haGroup=___DEFAULT___
                maxEventLoopExecuteTimeUnit=SECONDS
                blockedThreadCheckIntervalUnit=SECOND
                """);

        val configStone = getDiamondStone("DEFAULT");
        assertConfigValue(configStone);

        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testVertxOptionsConfigElfInEtcd() {
        MockEtcdServer.setUpMockServer();
        MockEtcdServer.addOrModifyProperty(VERTX_OPTIONS_ETCD_NAMESPACE, "DEFAULT", """
                eventLoopPoolSize=2
                maxEventLoopExecuteTime=5
                haEnabled=true
                haGroup=___DEFAULT___
                maxEventLoopExecuteTimeUnit=SECONDS
                blockedThreadCheckIntervalUnit=SECOND
                """);

        val configValue = getEtcdValue("DEFAULT");
        assertConfigValue(configValue);

        MockEtcdServer.tearDownMockServer();
    }

    private void assertConfigValue(String configValue) {
        assertNotNull(configValue);
        val vertxOptions = parsePropertiesToVertxOptions(parseStringToProperties(configValue));
        assertEquals(2, vertxOptions.getEventLoopPoolSize());
        assertEquals(5, vertxOptions.getMaxEventLoopExecuteTime());
        assertTrue(vertxOptions.isHAEnabled());
        assertEquals("___DEFAULT___", vertxOptions.getHAGroup());
        assertEquals(TimeUnit.SECONDS, vertxOptions.getMaxEventLoopExecuteTimeUnit());
        assertNull(vertxOptions.getBlockedThreadCheckIntervalUnit()); // error config SECOND, should be SECONDS
        assertNull(vertxOptions.getClusterManager());
    }
}
