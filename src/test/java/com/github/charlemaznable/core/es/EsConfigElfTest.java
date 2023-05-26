package com.github.charlemaznable.core.es;

import com.github.charlemaznable.apollo.MockApolloServer;
import com.github.charlemaznable.etcdconf.MockEtcdServer;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.n3r.diamond.client.impl.MockDiamondServer;

import java.time.Duration;

import static com.github.charlemaznable.core.es.EsClientElf.parsePropertiesToEsConfig;
import static com.github.charlemaznable.core.es.EsConfigElf.ES_CONFIG_DIAMOND_GROUP_NAME;
import static com.github.charlemaznable.core.es.EsConfigElf.ES_CONFIG_ETCD_NAMESPACE;
import static com.github.charlemaznable.core.es.EsConfigElf.getApolloProperty;
import static com.github.charlemaznable.core.es.EsConfigElf.getDiamondStone;
import static com.github.charlemaznable.core.es.EsConfigElf.getEtcdValue;
import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EsConfigElfTest {

    @Test
    public void testEsConfigElfInApollo() {
        MockApolloServer.setUpMockServer();

        val configProperty = getApolloProperty("default");
        assertConfigValue(configProperty);

        MockApolloServer.tearDownMockServer();
    }

    @Test
    public void testEsConfigElfInDiamond() {
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo(ES_CONFIG_DIAMOND_GROUP_NAME, "DEFAULT", """
                uris=http://localhost:9200,http://localhost:9201
                username=username
                password=pa55wOrd
                connectionTimeout=5
                socketTimeout=60
                """);

        val configStone = getDiamondStone("DEFAULT");
        assertConfigValue(configStone);

        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testEsConfigElfInEtcd() {
        MockEtcdServer.setUpMockServer();
        MockEtcdServer.addOrModifyProperty(ES_CONFIG_ETCD_NAMESPACE, "DEFAULT", """
                uris=http://localhost:9200,http://localhost:9201
                username=username
                password=pa55wOrd
                connectionTimeout=5
                socketTimeout=60
                """);

        val configValue = getEtcdValue("DEFAULT");
        assertConfigValue(configValue);

        MockEtcdServer.tearDownMockServer();
    }

    private void assertConfigValue(String configValue) {
        assertNotNull(configValue);
        val esConfig = parsePropertiesToEsConfig(parseStringToProperties(configValue));
        val uris = esConfig.getUris();
        assertEquals(2, uris.size());
        assertTrue(uris.contains("http://localhost:9200"));
        assertTrue(uris.contains("http://localhost:9201"));
        assertEquals("username", esConfig.getUsername());
        assertEquals("pa55wOrd", esConfig.getPassword());
        assertEquals(Duration.ofSeconds(5), esConfig.getConnectionTimeout());
        assertEquals(Duration.ofSeconds(60), esConfig.getSocketTimeout());
        assertNull(esConfig.getPathPrefix());
    }
}
