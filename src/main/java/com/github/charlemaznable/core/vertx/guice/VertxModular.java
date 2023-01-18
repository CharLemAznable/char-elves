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
        this(new AbstractModule() {
            @Override
            protected void configure() {
                bind(VertxOptions.class).toProvider(Providers.of(vertxOptions));
            }
        });
    }

    public VertxModular(Class<? extends Provider<VertxOptions>> vertxOptionsProviderClass) {
        this(new AbstractModule() {
            @Override
            protected void configure() {
                bind(VertxOptions.class).toProvider(vertxOptionsProviderClass);
            }
        });
    }

    public Module createModule() {
        return Modulee.combine(vertxOptionsModule, new AbstractModule() {
            @Provides
            @Singleton
            public Vertx vertx(@Nullable VertxOptions vertxOptions) {
                return buildVertx(nullThen(vertxOptions, VertxOptions::new));
            }
        });
    }
}
