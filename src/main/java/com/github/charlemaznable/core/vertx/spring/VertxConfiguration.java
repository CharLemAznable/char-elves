package com.github.charlemaznable.core.vertx.spring;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.vertx.VertxElf.buildVertx;

@Configuration
@Import({VertxConfiguration.RxVertxConfiguration.class,
        VertxConfiguration.Rx2VertxConfiguration.class,
        VertxConfiguration.Rx3VertxConfiguration.class})
public class VertxConfiguration {

    @Bean
    public Vertx vertx(@Nullable VertxOptions vertxOptions,
                       @Nullable ClusterManager clusterManager) {
        return buildVertx(nullThen(vertxOptions, VertxOptions::new), clusterManager);
    }

    @ConditionalOnRx("io.vertx.rxjava.core.Vertx")
    @Configuration
    public static class RxVertxConfiguration {

        @Bean("vertx.rx")
        public FactoryBean<Object> rxVertx(Vertx vertx) {
            return VertxRxJava.buildFactoryBean(vertx);
        }

        private static final class VertxRxJava {

            private static FactoryBean<Object> buildFactoryBean(Vertx vertx) {
                return new FactoryBean<>() {
                    @Override
                    public Object getObject() {
                        return new io.vertx.rxjava.core.Vertx(vertx);
                    }

                    @Override
                    public Class<?> getObjectType() {
                        return io.vertx.rxjava.core.Vertx.class;
                    }
                };
            }
        }
    }

    @ConditionalOnRx("io.vertx.reactivex.core.Vertx")
    @Configuration
    public static class Rx2VertxConfiguration {

        @Bean("vertx.rx2")
        public FactoryBean<Object> rx2Vertx(Vertx vertx) {
            return VertxRxJava2.buildFactoryBean(vertx);
        }

        private static final class VertxRxJava2 {

            private static FactoryBean<Object> buildFactoryBean(Vertx vertx) {
                return new FactoryBean<>() {
                    @Override
                    public Object getObject() {
                        return new io.vertx.reactivex.core.Vertx(vertx);
                    }

                    @Override
                    public Class<?> getObjectType() {
                        return io.vertx.reactivex.core.Vertx.class;
                    }
                };
            }
        }
    }

    @ConditionalOnRx("io.vertx.rxjava3.core.Vertx")
    @Configuration
    public static class Rx3VertxConfiguration {

        @Bean("vertx.rx3")
        public FactoryBean<Object> rx3Vertx(Vertx vertx) {
            return VertxRxJava3.buildFactoryBean(vertx);
        }

        private static final class VertxRxJava3 {

            private static FactoryBean<Object> buildFactoryBean(Vertx vertx) {
                return new FactoryBean<>() {
                    @Override
                    public Object getObject() {
                        return new io.vertx.rxjava3.core.Vertx(vertx);
                    }

                    @Override
                    public Class<?> getObjectType() {
                        return io.vertx.rxjava3.core.Vertx.class;
                    }
                };
            }
        }
    }
}
