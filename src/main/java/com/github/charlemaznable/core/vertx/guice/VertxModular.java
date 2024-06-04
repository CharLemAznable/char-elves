package com.github.charlemaznable.core.vertx.guice;

import com.github.charlemaznable.core.guice.Modulee;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Providers;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import lombok.AllArgsConstructor;

import javax.annotation.Nullable;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.vertx.VertxElf.buildVertx;

@AllArgsConstructor
public final class VertxModular {

    private final Module vertxOptionsModule;

    public VertxModular() {
        this((VertxOptions) null);
    }

    public VertxModular(VertxOptions vertxOptions) {
        this(vertxOptions, null);
    }

    public VertxModular(ClusterManager clusterManager) {
        this(null, clusterManager);
    }

    public VertxModular(VertxOptions vertxOptions, ClusterManager clusterManager) {
        this(new AbstractModule() {
            @Override
            protected void configure() {
                bind(VertxOptions.class).toProvider(Providers.of(vertxOptions));
                bind(ClusterManager.class).toProvider(Providers.of(clusterManager));
            }
        });
    }

    public VertxModular(Class<? extends Provider<VertxOptions>> vertxOptionsProviderClass,
                        Class<? extends Provider<ClusterManager>> clusterManagerProviderClass) {
        this(new AbstractModule() {
            @Override
            protected void configure() {
                bind(VertxOptions.class).toProvider(vertxOptionsProviderClass);
                bind(ClusterManager.class).toProvider(clusterManagerProviderClass);
            }
        });
    }

    public Module createModule() {
        return Modulee.combine(vertxOptionsModule, new AbstractModule() {
            @Provides
            @Singleton
            public Vertx vertx(@Nullable VertxOptions vertxOptions,
                               @Nullable ClusterManager clusterManager) {
                return buildVertx(nullThen(vertxOptions, VertxOptions::new), clusterManager);
            }
        });
    }
}
