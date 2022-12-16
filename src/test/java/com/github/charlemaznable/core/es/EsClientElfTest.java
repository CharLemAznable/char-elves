package com.github.charlemaznable.core.es;

import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.OpenRequest;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.charlemaznable.core.es.EsClientElf.buildElasticsearchAsyncClient;
import static com.github.charlemaznable.core.es.EsClientElf.buildElasticsearchClient;
import static com.github.charlemaznable.core.es.EsClientElf.closeElasticsearchApiClient;
import static com.github.charlemaznable.core.es.EsClientElf.parsePropertiesToEsConfig;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static com.google.common.collect.Lists.newArrayList;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EsClientElfTest {

    private static final String ELASTICSEARCH_VERSION = "7.17.6";
    private static final DockerImageName ELASTICSEARCH_IMAGE = DockerImageName
            .parse("docker.elastic.co/elasticsearch/elasticsearch")
            .withTag(ELASTICSEARCH_VERSION);

    private static final String ELASTICSEARCH_USERNAME = "elastic";
    private static final String ELASTICSEARCH_PASSWORD = "changeme";

    @Test
    public void testParseEsConfig() {
        val propertiesString = """
                uris=http://localhost:9200,http://localhost:9201
                username=username
                password=pa55wOrd
                connectionTimeout=5
                socketTimeout=60
                """;
        val properties = parseStringToProperties(propertiesString);
        val esConfig = parsePropertiesToEsConfig(properties);
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

    @SneakyThrows
    @Test
    public void testElasticsearchClient() {
        try (val elasticsearch = new ElasticsearchContainer(ELASTICSEARCH_IMAGE)
                .withPassword(ELASTICSEARCH_PASSWORD)) {
            elasticsearch.start();

            val esConfig = new EsConfig();
            esConfig.setUris(newArrayList(elasticsearch.getHttpHostAddress()));
            esConfig.setUsername(ELASTICSEARCH_USERNAME);
            esConfig.setPassword(ELASTICSEARCH_PASSWORD);
            val esClient = buildElasticsearchClient(esConfig);

            val createIndexRequest = CreateIndexRequest.of(builder -> builder.index("twitter"));
            val createIndexResponse = esClient.indices()
                    .create(createIndexRequest);
            assertTrue(createIndexResponse.acknowledged());
            assertTrue(createIndexResponse.shardsAcknowledged());

            val openRequest = OpenRequest.of(builder -> builder.index("twitter"));
            val openIndexResponse = esClient.indices().open(openRequest);
            assertTrue(openIndexResponse.acknowledged());
            assertTrue(openIndexResponse.shardsAcknowledged());

            val sourceMap = Maps.<String, Object>newHashMap();
            sourceMap.put("user", "kimchy");
            sourceMap.put("postDate", new Date());
            sourceMap.put("message", "trying out Elasticsearch");
            val indexRequest = IndexRequest.of(builder -> builder.index("twitter").id("1").document(sourceMap));
            val indexResponse = esClient.index(indexRequest);
            assertEquals("twitter", indexResponse.index());
            assertEquals("1", indexResponse.id());

            val getRequest = GetRequest.of(builder -> builder.index("twitter").id("1"));
            val getResponse = esClient.get(getRequest, Map.class);
            assertEquals("twitter", getResponse.index());
            assertEquals("1", getResponse.id());
            assertTrue(getResponse.found());
            val responseMap = newHashMap(getResponse.source());
            assertEquals(sourceMap.get("user"), responseMap.get("user"));
            assertEquals(sourceMap.get("postDate"), parseResponsePostDate(responseMap));
            assertEquals(sourceMap.get("message"), responseMap.get("message"));

            closeElasticsearchApiClient(esClient);
        }
    }

    @SneakyThrows
    @Test
    public void testElasticsearchAsyncClient() {
        try (val elasticsearch = new ElasticsearchContainer(ELASTICSEARCH_IMAGE)
                .withPassword(ELASTICSEARCH_PASSWORD)) {
            elasticsearch.start();

            val esConfig = new EsConfig();
            esConfig.setUris(newArrayList(elasticsearch.getHttpHostAddress()));
            esConfig.setUsername(ELASTICSEARCH_USERNAME);
            esConfig.setPassword(ELASTICSEARCH_PASSWORD);
            val esClient = buildElasticsearchAsyncClient(esConfig);

            AtomicBoolean createDone = new AtomicBoolean(false);
            val createIndexRequest = CreateIndexRequest.of(builder -> builder.index("twitter"));
            esClient.indices().create(createIndexRequest)
                    .whenComplete((createIndexResponse, exception) -> {
                        assertTrue(createIndexResponse.acknowledged());
                        assertTrue(createIndexResponse.shardsAcknowledged());
                        createDone.set(true);
                    });
            await().untilTrue(createDone);

            AtomicBoolean openDone = new AtomicBoolean(false);
            val openRequest = OpenRequest.of(builder -> builder.index("twitter"));
            esClient.indices().open(openRequest)
                    .whenComplete((openIndexResponse, exception) -> {
                        assertTrue(openIndexResponse.acknowledged());
                        assertTrue(openIndexResponse.shardsAcknowledged());
                        openDone.set(true);
                    });
            await().untilTrue(openDone);

            AtomicBoolean indexDone = new AtomicBoolean(false);
            val sourceMap = Maps.<String, Object>newHashMap();
            sourceMap.put("user", "kimchy");
            sourceMap.put("postDate", new Date());
            sourceMap.put("message", "trying out Elasticsearch");
            val indexRequest = IndexRequest.of(builder -> builder.index("twitter").id("1").document(sourceMap));
            esClient.index(indexRequest)
                    .whenComplete((indexResponse, exception) -> {
                        assertEquals("twitter", indexResponse.index());
                        assertEquals("1", indexResponse.id());
                        indexDone.set(true);
                    });
            await().untilTrue(indexDone);

            AtomicBoolean getDone = new AtomicBoolean(false);
            val getRequest = GetRequest.of(builder -> builder.index("twitter").id("1"));
            esClient.get(getRequest, Map.class)
            .whenComplete((getResponse, exception) -> {
                assertEquals("twitter", getResponse.index());
                assertEquals("1", getResponse.id());
                assertTrue(getResponse.found());
                val responseMap = newHashMap(getResponse.source());
                assertEquals(sourceMap.get("user"), responseMap.get("user"));
                assertEquals(sourceMap.get("postDate"), parseResponsePostDate(responseMap));
                assertEquals(sourceMap.get("message"), responseMap.get("message"));
                getDone.set(true);
            });
            await().untilTrue(getDone);

            closeElasticsearchApiClient(esClient);
        }
    }

    @Test
    public void testClose() {
        assertDoesNotThrow(() -> closeElasticsearchApiClient(null));
    }

    @SneakyThrows
    private Date parseResponsePostDate(Map<?, ?> responseMap) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                .parse(responseMap.get("postDate").toString());
    }
}
