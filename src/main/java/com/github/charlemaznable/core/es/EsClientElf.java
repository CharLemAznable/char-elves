package com.github.charlemaznable.core.es;

import co.elastic.clients.ApiClient;
import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.github.charlemaznable.core.es.EsClientBuildElf.ConfigCredentialsProvider;
import com.google.common.base.Splitter;
import com.google.common.primitives.Primitives;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Objectt.setValue;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EsClientElf {

    public static EsConfig parsePropertiesToEsConfig(Properties properties) {
        val esConfig = new EsConfig();
        for (val prop : properties.entrySet()) {
            setValue(esConfig, Objects.toString(prop.getKey()), returnType -> {
                val value = Objects.toString(prop.getValue());
                val rt = Primitives.unwrap(checkNotNull(returnType));
                if (rt == String.class) return value;
                if (rt == Duration.class)
                    return Duration.ofSeconds(Long.parseLong(value));
                if (rt == List.class)
                    return Splitter.on(",").omitEmptyStrings()
                            .trimResults().splitToList(value);
                return null;
            });
        }
        return esConfig;
    }

    public static ElasticsearchClient buildElasticsearchClient(EsConfig esConfig) {
        return new ElasticsearchClient(new RestClientTransport(
                buildEsHttpClient(esConfig), buildJacksonJsonpMapper()));
    }

    public static ElasticsearchAsyncClient buildElasticsearchAsyncClient(EsConfig esConfig) {
        return new ElasticsearchAsyncClient(new RestClientTransport(
                buildEsHttpClient(esConfig), buildJacksonJsonpMapper()));
    }

    @SneakyThrows
    public static void closeElasticsearchApiClient(@SuppressWarnings("rawtypes") ApiClient client) {
        if (isNull(client) || isNull(client._transport())) return;
        client._transport().close();
    }

    private static RestClient buildEsHttpClient(EsConfig esConfig) {
        val hosts = esConfig.getUris().stream()
                .map(EsClientBuildElf::createHttpHost).toArray(HttpHost[]::new);
        val builder = RestClient.builder(hosts);
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setDefaultCredentialsProvider(
                    new ConfigCredentialsProvider(esConfig));
            return httpClientBuilder;
        });
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            val connectionTimeout = esConfig.getConnectionTimeout();
            if (nonNull(connectionTimeout)) {
                requestConfigBuilder.setConnectTimeout(Long.valueOf(
                        connectionTimeout.toMillis()).intValue());
            }
            val socketTimeout = esConfig.getSocketTimeout();
            if (nonNull(socketTimeout)) {
                requestConfigBuilder.setSocketTimeout(Long.valueOf(
                        socketTimeout.toMillis()).intValue());
            }
            return requestConfigBuilder;
        });
        if (nonNull(esConfig.getPathPrefix())) {
            builder.setPathPrefix(esConfig.getPathPrefix());
        }
        return builder.build();
    }

    private static JacksonJsonpMapper buildJacksonJsonpMapper() {
        val objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new StdDateFormat());
        return new JacksonJsonpMapper(objectMapper);
    }
}
