package com.github.charlemaznable.core.es;

import com.github.charlemaznable.core.es.EsClientBuildElf.ConfigCredentialsProvider;
import com.google.common.base.Splitter;
import com.google.common.primitives.Primitives;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Objectt.setValue;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@SuppressWarnings("deprecation")
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
