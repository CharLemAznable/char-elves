package com.github.charlemaznable.core.vertx.spring;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.vertx.VertxElf.buildVertx;

@Configuration
public class VertxConfiguration {

    @Bean
    public Vertx vertx(@Nullable VertxOptions vertxOptions) {
        return buildVertx(nullThen(vertxOptions, VertxOptions::new));
    }

    @ConditionalOnRx(io.vertx.rxjava.core.Vertx.class)
    @Bean("vertx.rx")
    public io.vertx.rxjava.core.Vertx rxVertx(Vertx vertx) {
        return new io.vertx.rxjava.core.Vertx(vertx);
    }

    @ConditionalOnRx(io.vertx.reactivex.core.Vertx.class)
    @Bean("vertx.rx2")
    public io.vertx.reactivex.core.Vertx rx2Vertx(Vertx vertx) {
        return new io.vertx.reactivex.core.Vertx(vertx);
    }

    @ConditionalOnRx(io.vertx.rxjava3.core.Vertx.class)
    @Bean("vertx.rx3")
    public io.vertx.rxjava3.core.Vertx rx3Vertx(Vertx vertx) {
        return new io.vertx.rxjava3.core.Vertx(vertx);
    }
}
