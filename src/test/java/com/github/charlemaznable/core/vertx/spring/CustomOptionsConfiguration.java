package com.github.charlemaznable.core.vertx.spring;

import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@VertxImport
public class CustomOptionsConfiguration {

    static final int DEFAULT_WORKER_POOL_SIZE = 42;

    @Bean
    public VertxOptions vertxOptions() {
        return new VertxOptions().setWorkerPoolSize(DEFAULT_WORKER_POOL_SIZE);
    }

    @Bean
    public ClusterManager clusterManager() {
        return new HazelcastClusterManager();
    }
}
