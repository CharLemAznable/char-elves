package com.github.charlemaznable.core.vertx.spring;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.vertx.VertxElf.buildVertx;

@Configuration
public class VertxConfiguration {

    @Bean
    public Vertx vertx(@Autowired(required = false) VertxOptions vertxOptions) {
        return buildVertx(nullThen(vertxOptions, VertxOptions::new));
    }
}
