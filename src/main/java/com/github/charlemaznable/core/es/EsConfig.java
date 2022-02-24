package com.github.charlemaznable.core.es;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class EsConfig {

    /**
     * Comma-separated list of the Elasticsearch instances to use.
     */
    private List<String> uris = new ArrayList<>(Collections.singletonList("http://localhost:9200"));

    /**
     * Username for authentication with Elasticsearch.
     */
    private String username;

    /**
     * Password for authentication with Elasticsearch.
     */
    private String password;

    /**
     * Connection timeout used when communicating with Elasticsearch.
     */
    private Duration connectionTimeout = Duration.ofSeconds(1);

    /**
     * Socket timeout used when communicating with Elasticsearch.
     */
    private Duration socketTimeout = Duration.ofSeconds(30);

    /**
     * Prefix added to the path of every request sent to Elasticsearch.
     */
    private String pathPrefix;
}
