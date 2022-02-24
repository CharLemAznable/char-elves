package com.github.charlemaznable.core.es;

import com.github.charlemaznable.core.es.EsClientBuildElf.ConfigCredentialsProvider;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EsClientElf {

    public static RestHighLevelClient buildEsClient(EsConfig esConfig) {
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
                requestConfigBuilder.setConnectTimeout(new Long(
                        connectionTimeout.toMillis()).intValue());
            }
            val socketTimeout = esConfig.getSocketTimeout();
            if (nonNull(socketTimeout)) {
                requestConfigBuilder.setSocketTimeout(new Long(
                        socketTimeout.toMillis()).intValue());
            }
            return requestConfigBuilder;
        });
        if (nonNull(esConfig.getPathPrefix())) {
            builder.setPathPrefix(esConfig.getPathPrefix());
        }
        return new RestHighLevelClient(builder);
    }

    @SneakyThrows
    public static void closeEsClient(RestHighLevelClient client) {
        if (isNull(client)) return;
        client.close();
    }
}
