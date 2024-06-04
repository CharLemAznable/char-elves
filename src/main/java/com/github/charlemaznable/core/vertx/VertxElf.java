package com.github.charlemaznable.core.vertx;

import com.google.common.primitives.Primitives;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxBuilder;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.github.charlemaznable.core.lang.Clz.isAssignable;
import static com.github.charlemaznable.core.lang.Objectt.parseObject;
import static com.github.charlemaznable.core.lang.Objectt.setValue;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@NoArgsConstructor(access = PRIVATE)
public final class VertxElf {

    private static final String CLUSTER_MANAGER_CLASS_PROPERTY = "vertx.cluster.managerClass";

    public static VertxOptions parsePropertiesToVertxOptions(Properties properties) {
        val vertxOptions = new VertxOptions();
        for (val prop : properties.entrySet()) {
            setValue(vertxOptions, Objects.toString(prop.getKey()), returnType -> {
                if (isNull(returnType)) return prop.getValue();
                val value = Objects.toString(prop.getValue());
                val rt = Primitives.unwrap(returnType);
                if (rt == String.class) return value;
                if (rt.isPrimitive()) return parsePrimitive(rt, value);
                if (isAssignable(rt, Enum.class)) return parseEnum(rt, value);
                return parseObject(value, rt);
            });
        }
        return vertxOptions;
    }

    public static boolean isClustered(VertxOptions vertxOptions) {
        return isClustered(vertxOptions, null);
    }

    public static boolean isClustered(VertxOptions vertxOptions, ClusterManager clusterManager) {
        return nonNull(vertxOptions.getClusterManager()) || nonNull(clusterManager) ||
                nonNull(System.getProperty(CLUSTER_MANAGER_CLASS_PROPERTY));
    }

    public static Vertx buildVertx(VertxBuilder vertxBuilder, boolean clustered) {
        return buildVertx(vertxBuilder, clustered, null);
    }

    @SneakyThrows
    public static Vertx buildVertx(VertxBuilder vertxBuilder, boolean clustered, Function<Throwable, Vertx> exceptionFn) {
        if (clustered) {
            val cf = new CompletableFuture<Vertx>();
            val completableFuture = isNull(exceptionFn) ? cf : cf.exceptionally(exceptionFn);
            vertxBuilder.buildClustered(asyncResult -> {
                if (asyncResult.failed()) {
                    completableFuture.completeExceptionally(asyncResult.cause());
                } else {
                    completableFuture.complete(asyncResult.result());
                }
            });
            return completableFuture.get();
        } else {
            return vertxBuilder.build();
        }
    }

    public static Vertx buildVertx(VertxOptions vertxOptions) {
        return buildVertx(Vertx.builder().with(vertxOptions), isClustered(vertxOptions));
    }

    public static Vertx buildVertx(VertxOptions vertxOptions, ClusterManager clusterManager) {
        return buildVertx(Vertx.builder().with(vertxOptions).withClusterManager(clusterManager),
                isClustered(vertxOptions, clusterManager));
    }

    public static Vertx buildVertx(VertxOptions vertxOptions, Function<Throwable, Vertx> exceptionFn) {
        return buildVertx(Vertx.builder().with(vertxOptions), isClustered(vertxOptions), exceptionFn);
    }

    public static Vertx buildVertx(VertxOptions vertxOptions, ClusterManager clusterManager, Function<Throwable, Vertx> exceptionFn) {
        return buildVertx(Vertx.builder().with(vertxOptions).withClusterManager(clusterManager),
                isClustered(vertxOptions, clusterManager), exceptionFn);
    }

    public static void closeVertx(Vertx vertx) {
        closeVertx(vertx, null);
    }

    @SneakyThrows
    public static void closeVertx(Vertx vertx, Function<Throwable, Void> exceptionFn) {
        if (isNull(vertx)) return;

        val cf = new CompletableFuture<Void>();
        val completableFuture = isNull(exceptionFn)
                ? cf : cf.exceptionally(exceptionFn);
        vertx.close(asyncResult -> {
            if (asyncResult.failed()) {
                completableFuture.completeExceptionally(asyncResult.cause());
            } else {
                completableFuture.complete(asyncResult.result());
            }
        });
        completableFuture.get();
    }

    public static void closeVertxImmediately(Vertx vertx) {
        if (isNull(vertx)) return;
        vertx.close();
    }

    public static <V> void executeBlocking(
            Callable<V> blockingCodeHandler,
            Handler<AsyncResult<V>> resultHandler) {
        Vertx.currentContext()
                .executeBlocking(blockingCodeHandler, false)
                .onComplete(resultHandler);
    }

    private static Object parsePrimitive(Class<?> rt, String value) {
        if (rt == boolean.class) return toBoolean(value);
        if (rt == int.class) return toInt(value);
        if (rt == long.class) return toLong(value);
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object parseEnum(Class<?> rt, String value) {
        try {
            return Enum.valueOf((Class<Enum>) rt, value);
        } catch (Exception e) {
            return null;
        }
    }
}
